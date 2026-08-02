package com.cinema.seat_service.service;

import com.cinema.seat_service.dto.LockSeatsRequest;
import com.cinema.seat_service.dto.SeatResponse;
import com.cinema.seat_service.dto.ShowtimeSeatResponse;
import com.cinema.seat_service.dto.UpdateSeatRequest;
import com.cinema.seat_service.entity.ReservationStatus;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.entity.SeatReservation;
import com.cinema.seat_service.repository.SeatRepository;
import com.cinema.seat_service.repository.SeatReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SeatServiceImplementation implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;

    public SeatServiceImplementation(SeatRepository seatRepository, SeatReservationRepository seatReservationRepository) {
        this.seatRepository = seatRepository;
        this.seatReservationRepository = seatReservationRepository;
    }

    @Override
    public Page<SeatResponse> getAllSeatsByHallId(UUID hallId, Pageable pageable) {
        log.info("Fetching seats for hall with id {}" , hallId);
        Page<Seat> seats = seatRepository.findByHallId(hallId, pageable);
        log.info("Fetched seats for hall with id {} successfully" , hallId);
        return seats.map(this::convertToResponse);

    }

    @Override
    public SeatResponse getSeatsById(UUID id) {
        log.info("Fetching seat with id {}" , id);
        Seat seat = seatRepository.findById(id).orElseThrow(() -> {
            log.warn("Seat with id {} was not found" , id);
            return new RuntimeException("Seat not found with id " + id);
        });
        log.info("Fetched seat with id {} successfully" , id);
        return convertToResponse(seat);
    }

    @Override
    public SeatResponse updateSeat(UUID id, UpdateSeatRequest request) {
        log.info("Updating seat with id {}" , id);
        Seat seat = seatRepository.findById(id).orElseThrow(() -> {
            log.warn("while updating Seat with id {} was not found" , id);
            return new RuntimeException("Seat not found with id " + id);
        });
        // Update seat properties based on the request
       seat.setSeatType(request.getSeatType());
        Seat updatedSeat = seatRepository.save(seat);
        log.info("Updated seat with id {} successfully" , id);
        return convertToResponse(updatedSeat);

    }

    @Override
    public void deleteSeatById(UUID id) {
        log.info("Deleting seat with id {}" , id);
        seatRepository.deleteById(id);
        log.info("Deleted seat with id {} successfully" , id);
    }

    @Override
    public List<ShowtimeSeatResponse>  getSeatMapForShowtime(UUID hallId, UUID showtimeId){
        log.info("Fetching dynamic seat map for hallId {} and showtimeId {}", hallId, showtimeId);
        List<Seat> physicalSeats = seatRepository.findByHallId(hallId, Pageable.unpaged()).getContent();
        List<SeatReservation> activeReservations  =  seatReservationRepository.findActiveReservations(showtimeId , LocalDateTime.now());
        Map<UUID , String> reservationStatusMap = activeReservations.stream()
                .collect(Collectors.toMap(SeatReservation::getSeatId, res->res.getStatus().name()));

        return physicalSeats.stream().map(seat -> ShowtimeSeatResponse.builder()
                .seatId(seat.getId())
                .rowName(seat.getRowName())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .status(reservationStatusMap.getOrDefault(seat.getId(), "AVAILABLE"))
                .build())
                .collect(Collectors.toList());

    }

    public List<UUID> lockSeatsForCheckout(LockSeatsRequest request) {
        log.info("Attempting to lock {} seats for showtimeId {}", request.getSeatIds().size(), request.getShowtimeId());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(10); // Hold seats for 10 minutes

        // Fetch existing active locks/bookings
        List<SeatReservation> activeReservations =
                seatReservationRepository.findActiveReservations(request.getShowtimeId(), now);

        Set<UUID> unavailableSeatIds = activeReservations.stream()
                .map(SeatReservation::getSeatId)
                .collect(Collectors.toSet());

        List<UUID> newlyLockedSeats = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {
            if (unavailableSeatIds.contains(seatId)) {
                log.warn("Seat {} is already locked/booked for showtime {}", seatId, request.getShowtimeId());
                continue;
            }

            SeatReservation lock = SeatReservation.builder()
                    .showtimeId(request.getShowtimeId())
                    .seatId(seatId)
                    .userId(request.getUserId())
                    .status(ReservationStatus.LOCKED)
                    .lockExpiration(expirationTime)
                    .build();

            seatReservationRepository.save(lock);
            newlyLockedSeats.add(seatId);
        }

        return newlyLockedSeats;
    }


    private SeatResponse convertToResponse(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getHallId(),
                seat.getRowName(),
                seat.getSeatNumber(),
                seat.getSeatType()

        );
    }
}
