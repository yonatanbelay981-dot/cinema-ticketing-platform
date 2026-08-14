package com.cinema.seat_service.service;

import com.cinema.seat_service.dto.LockSeatsRequest;
import com.cinema.seat_service.entity.ReservationStatus;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.entity.SeatReservation;
import com.cinema.seat_service.event.BookEvent;
import com.cinema.seat_service.exception.SeatNotFoundException;
import com.cinema.seat_service.exception.SeatNotHallException;
import com.cinema.seat_service.exception.SeatNotShowTimeException;
import com.cinema.seat_service.exception.SeatUnavailableException;
import com.cinema.seat_service.repository.SeatRepository;
import com.cinema.seat_service.repository.SeatReservationRepository;
import com.cinema.seat_service.repository.ShowtimeCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Slf4j
public class BookingEventListener {
    private final ShowtimeCacheRepository showtimeCacheRepository;
    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;

    public BookingEventListener(ShowtimeCacheRepository showtimeCacheRepository, SeatRepository seatRepository, SeatReservationRepository seatReservationRepository) {
        this.showtimeCacheRepository = showtimeCacheRepository;
        this.seatRepository = seatRepository;
        this.seatReservationRepository = seatReservationRepository;
    }

    @KafkaListener(topics = "booking-events", groupId = "seat-service")
    public void ListenBookingEvent(BookEvent bookEvent){
        switch (bookEvent.getEventType()) {
            case "LOCK_SEATS_REQUESTED":
                lockSeats(bookEvent.getSeatIds() , );
                break;
        }

    }

    public void lockSeats(List<UUID> seatIds , LockSeatsRequest request) {

        log.info("Attempting to lock {} seats for showtime {}",
                request.getSeatIds().size(),
                request.getShowtimeId());

        // Verify the showtime exists
        var showtime = showtimeCacheRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> {
                    log.warn("Showtime {} was not found", request.getShowtimeId());
                    return new SeatNotShowTimeException(
                            "Showtime not found with id " + request.getShowtimeId());
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusMinutes(10);

        // Fetch active reservations
        List<SeatReservation> activeReservations =
                seatReservationRepository.findByShowtimeIdAndStatusInAndLockExpirationAfter(
                        request.getShowtimeId(),
                        now
                );

        Set<UUID> unavailableSeatIds = activeReservations.stream()
                .map(SeatReservation::getSeatId)
                .collect(Collectors.toSet());

        List<UUID> lockedSeatIds = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {

            // Verify seat exists
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> {
                        log.warn("Seat {} was not found", seatId);
                        return new SeatNotFoundException(
                                "Seat not found with id " + seatId);
                    });

            // Verify seat belongs to the same hall as the showtime
            if (!seat.getHallId().equals(showtime.getHallId())) {
                log.warn("Seat {} does not belong to hall {}", seatId, showtime.getHallId());
                throw new SeatNotHallException(
                        "Seat " + seatId + " does not belong to the hall of this showtime."
                );
            }

            // Verify seat isn't already locked/booked
            if (unavailableSeatIds.contains(seatId)) {
                log.warn("Seat {} is unavailable", seatId);
                throw new SeatUnavailableException(
                        "Seat " + seatId + " is already locked or booked."
                );
            }

            SeatReservation reservation = SeatReservation.builder()
                    .showtimeId(request.getShowtimeId())
                    .seatId(seatId)
                    .userId(request.getUserId())
                    .status(ReservationStatus.LOCKED)
                    .lockExpiration(expiration)
                    .build();

            seatReservationRepository.save(reservation);

            lockedSeatIds.add(seatId);
        }

        log.info("Successfully locked {} seats", lockedSeatIds.size());
    }
}
