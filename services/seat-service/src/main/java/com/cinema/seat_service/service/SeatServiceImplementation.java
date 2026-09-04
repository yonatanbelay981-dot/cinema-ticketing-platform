package com.cinema.seat_service.service;
import com.cinema.seat_service.dto.*;
import com.cinema.seat_service.entity.ReservationStatus;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.entity.SeatReservation;
import com.cinema.seat_service.event.BookEvent;
import com.cinema.seat_service.event.SeatEvent;
import com.cinema.seat_service.exception.SeatNotFoundException;
import com.cinema.seat_service.exception.SeatNotHallException;
import com.cinema.seat_service.exception.SeatNotShowTimeException;
import com.cinema.seat_service.exception.SeatUnavailableException;
import com.cinema.seat_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeatServiceImplementation implements SeatService {
    @Value("${app.seat.lock-duration-minutes}")
    private long seatLockDurationMinutes;

    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final ShowtimeCacheRepository showtimeCacheRepository;
    private final SeatKafkaProducer seatKafkaProducer;
    private final SeatLockRedisService seatLockRedisService;
    private final PhysicalSeatCacheService physicalSeatCacheService;

    public SeatServiceImplementation(
            SeatRepository seatRepository,
            SeatReservationRepository seatReservationRepository,
            ShowtimeCacheRepository showtimeCacheRepository,
            SeatKafkaProducer seatKafkaProducer, SeatLockRedisService seatLockRedisService, PhysicalSeatCacheService physicalSeatCacheService
    ) {
        this.seatRepository = seatRepository;
        this.seatReservationRepository = seatReservationRepository;
        this.showtimeCacheRepository = showtimeCacheRepository;
        this.seatKafkaProducer = seatKafkaProducer;
        this.seatLockRedisService = seatLockRedisService;
        this.physicalSeatCacheService = physicalSeatCacheService;
    }


    @Override
    public List<SeatResponse> getAllSeatsByHallId(
            UUID hallId
    ) {

        log.info(
                "Fetching seats for hall with id {}",
                hallId
        );

        List<Seat> seats =
                seatRepository.findByHallId(hallId);

        log.info(
                "Fetched seats for hall with id {} successfully",
                hallId
        );

        return  seats.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SeatResponse> getByHallIdRowNameAndSeatNumber(
            UUID hallId,
            String rowName,
            Integer seatNumber
    ) {

        log.info(
                "Fetching seat for hallId {}, rowName {}, seatNumber {}",
                hallId,
                rowName,
                seatNumber
        );

        return seatRepository
                .findByHallIdAndRowNameAndSeatNumber(
                        hallId,
                        rowName,
                        seatNumber
                )
                .map(this::convertToResponse);
    }

    @Override
    public SeatResponse getSeatsById(UUID id) {

        log.info(
                "Fetching seat with id {}",
                id
        );

        Seat seat = seatRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "while trying to get Seat with id {} was not found",
                            id
                    );

                    return new SeatNotFoundException(
                            "Seat not found with id " + id
                    );
                });

        return convertToResponse(seat);
    }

    @Override
    public SeatResponse updateSeat(
            UUID id,
            UpdateSeatRequest request
    ) {

        log.info(
                "Updating seat with id {}",
                id
        );

        Seat seat = seatRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "while trying to find Seat with id {} was not found",
                            id
                    );

                    return new SeatNotFoundException(
                            "Seat not found with id " + id
                    );
                });

        seat.setSeatType(request.getSeatType());

        Seat updatedSeat =
                seatRepository.save(seat);

        log.info(
                "Updated seat with id {} successfully",
                id
        );

        return convertToResponse(updatedSeat);
    }

    @Override
    public void deleteSeatById(UUID id) {

        log.info(
                "Deleting seat with id {}",
                id
        );

        Seat seat = seatRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Seat with id {} was not found",
                            id
                    );

                    return new SeatNotFoundException(
                            "Seat not found with id " + id
                    );
                });

        seatRepository.delete(seat);

        log.info(
                "Deleted seat with id {} successfully",
                id
        );
    }


    @Override
    public List<ShowtimeSeatResponse> getSeatMapForShowtime(
            UUID hallId,
            UUID showtimeId
    ) {

        log.info(
                "Fetching dynamic seat map for hallId {} and showtimeId {}",
                hallId,
                showtimeId
        );

        List<SeatResponse> physicalSeats =

        physicalSeatCacheService.getPhysicalSeats(hallId);


        List<SeatReservation> reservations =
                seatReservationRepository
                        .findByShowtimeIdAndStatusIn(
                                showtimeId,
                                List.of(
                                        ReservationStatus.LOCKED,
                                        ReservationStatus.BOOKED
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        Map<UUID, String> reservationStatusMap =
                reservations.stream()
                        .filter(reservation -> {

                            if (reservation.getStatus()
                                    == ReservationStatus.BOOKED) {

                                return true;
                            }

                            return reservation.getLockExpiration() != null
                                    && reservation
                                    .getLockExpiration()
                                    .isAfter(now);
                        })
                        .collect(Collectors.toMap(
                                SeatReservation::getSeatId,
                                reservation ->
                                        reservation
                                                .getStatus()
                                                .name(),

                                (existing, replacement) -> existing
                        ));

        return physicalSeats.stream()
                .map(seat ->
                        ShowtimeSeatResponse.builder()
                                .seatId(seat.getId())
                                .rowName(seat.getRowName())
                                .seatNumber(seat.getSeatNumber())
                                .seatType(seat.getSeatType())
                                .status(
                                        reservationStatusMap.getOrDefault(
                                                seat.getId(),
                                                "AVAILABLE"
                                        )
                                )
                                .build()
                )
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public List<UUID> lockSeatsForCheckout(
            BookEvent bookEvent
    ) {
      List<UUID> acquiredRedisLocked = new ArrayList<>();
        try {

            log.info(
                    "Attempting to lock {} seats for showtime {}",
                    bookEvent.getSeatIds().size(),
                    bookEvent.getShowTimeId()
            );

            List<UUID> requestedSeatIds =
                    bookEvent.getSeatIds()
                            .stream()
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());

            if (requestedSeatIds.isEmpty()) {

                throw new SeatUnavailableException(
                        "At least one seat must be selected."
                );
            }

            var showtime =
                    showtimeCacheRepository
                            .findById(bookEvent.getShowTimeId())
                            .orElseThrow(() -> {

                                log.warn(
                                        "Showtime {} was not found",
                                        bookEvent.getShowTimeId()
                                );

                                return new SeatNotShowTimeException(
                                        "Showtime not found with id "
                                                + bookEvent.getShowTimeId()
                                );
                            });

            LocalDateTime now =
                    LocalDateTime.now();

            LocalDateTime expiration =
                    now.plusMinutes(seatLockDurationMinutes);

            for (UUID seatId : requestedSeatIds) {

                Seat seat =
                        seatRepository
                                .findByIdForUpdate(seatId)
                                .orElseThrow(() -> {

                                    log.warn(
                                            "Seat {} was not found",
                                            seatId
                                    );

                                    return new SeatNotFoundException(
                                            "Seat not found with id "
                                                    + seatId
                                    );
                                });


                if (!seat.getHallId()
                        .equals(showtime.getHallId())) {

                    log.warn(
                            "Seat {} does not belong to hall {}",
                            seatId,
                            showtime.getHallId()
                    );

                    throw new SeatNotHallException(
                            "Seat " + seatId
                                    + " does not belong to the hall "
                                    + "of this showtime."
                    );
                }




                Optional<SeatReservation> existingReservation =
                        seatReservationRepository
                                .findByShowtimeIdAndSeatIdAndStatusIn(
                                        bookEvent.getShowTimeId(),
                                        seatId,
                                        List.of(
                                                ReservationStatus.LOCKED,
                                                ReservationStatus.BOOKED
                                        )

                                );

                if (existingReservation.isPresent()) {

                    SeatReservation reservation =
                            existingReservation.get();
                    if (reservation.getStatus()
                            == ReservationStatus.BOOKED) {

                        log.warn(
                                "Seat {} is already booked for showtime {}",
                                seatId,
                                bookEvent.getShowTimeId()
                        );

                        throw new SeatUnavailableException(
                                "Seat " + seatId
                                        + " is already booked."
                        );
                    }


                    if (reservation.getStatus()
                            == ReservationStatus.LOCKED) {

                        LocalDateTime lockExpiration =
                                reservation.getLockExpiration();

                        if (reservation.getBookingId()
                                .equals(bookEvent.getBookingId())) {

                            if (lockExpiration != null
                                    && lockExpiration.isAfter(now)) {

                                log.info(
                                        "Seat {} is already locked for booking {}. " +
                                                "Treating duplicate LOCK_SEATS_REQUESTED as success.",
                                        seatId,
                                        bookEvent.getBookingId()
                                );

                                continue;
                            }
                        }


                        if (lockExpiration != null
                                && lockExpiration.isAfter(now)) {

                            log.warn(
                                    "Seat {} is locked until {}",
                                    seatId,
                                    lockExpiration
                            );

                            throw new SeatUnavailableException(
                                    "Seat " + seatId
                                            + " is currently locked."
                            );
                        }


                        log.info(
                                "Lock for seat {} expired. Removing old reservation.",
                                seatId
                        );

                        UUID oldBookingId = reservation.getBookingId();

                        seatReservationRepository.delete(reservation);
                        seatReservationRepository.flush();

                        seatLockRedisService.releaseLock(
                                bookEvent.getShowTimeId(),
                                seatId,
                                oldBookingId
                        );
                    }
                }
                boolean redisLockAcquired = seatLockRedisService.tryLock(
                        bookEvent.getShowTimeId(),
                        seatId,
                        bookEvent.getBookingId(),
                        seatLockDurationMinutes
                );

                if (!redisLockAcquired) {

                    log.warn(
                            "Seat {} is already temporarily locked in Redis",
                            seatId
                    );
                    throw
                            new SeatUnavailableException( "Seat " + seatId + " is currently locked." );

                }

                acquiredRedisLocked.add(seatId);

                SeatReservation reservation =
                        SeatReservation.builder()
                                .bookingId(
                                        bookEvent.getBookingId()
                                )
                                .showtimeId(
                                        bookEvent.getShowTimeId()
                                )
                                .seatId(seatId)
                                .keycloakUserId(
                                        bookEvent.getKeycloakUserId()
                                )
                                .status(
                                        ReservationStatus.LOCKED
                                )
                                .lockExpiration(expiration)
                                .build();

                seatReservationRepository.save(
                        reservation
                );
                seatReservationRepository.flush();
            }

            log.info(
                    "Successfully locked {} seats for booking {}",
                    requestedSeatIds.size(),
                    bookEvent.getBookingId()
            );


            publishSeatEvent(
                    new SeatEvent(
                            SeatEvent.EventType.SEAT_LOCKED,
                            bookEvent.getBookingId(),
                            bookEvent.getShowTimeId(),
                            requestedSeatIds,
                            bookEvent.getKeycloakUserId()
                    )
            );

            return requestedSeatIds;

        } catch (SeatUnavailableException e) {
            for (UUID seatId : acquiredRedisLocked){
                seatLockRedisService.releaseLock(
                        bookEvent.getShowTimeId(),
                        seatId,
                        bookEvent.getBookingId()
                );
            }

            log.warn(
                    "Failed to lock seats for booking {}: {}",
                    bookEvent.getBookingId(),
                    e.getMessage()
            );

            publishSeatEvent(
                    new SeatEvent(
                            SeatEvent.EventType.LOCK_FAILED,
                            bookEvent.getBookingId(),
                            bookEvent.getShowTimeId(),
                            bookEvent.getSeatIds(),
                            bookEvent.getKeycloakUserId()
                    )
            );

            throw e;
        }
    }


    @Override
    @Transactional
    public void bookSeats(BookEvent event) {

        log.info(
                "Booking seats for bookingId {}",
                event.getBookingId()
        );

        List<SeatReservation> reservations =
                new ArrayList<>();

        LocalDateTime now =
                LocalDateTime.now();

        for (UUID seatId : event.getSeatIds()) {

            SeatReservation reservation =
                    seatReservationRepository
                            .findBySeatIdAndShowtimeIdAndBookingIdAndStatus(
                                    seatId,
                                    event.getShowTimeId(),
                                    event.getBookingId(),
                                    ReservationStatus.LOCKED
                            )
                            .orElseThrow(() -> {

                                log.warn(
                                        "Locked reservation not found for seat {}",
                                        seatId
                                );

                                return new SeatUnavailableException(
                                        "Seat " + seatId
                                                + " is not locked."
                                );
                            });


            if (!reservation.getKeycloakUserId()
                    .equals(event.getKeycloakUserId())) {

                log.warn(
                        "User {} does not own the lock for seat {}",
                        event.getKeycloakUserId(),
                        seatId
                );

                throw new SeatUnavailableException(
                        "You do not own the lock for seat "
                                + seatId
                );
            }

            if (reservation.getLockExpiration() == null
                    || !reservation
                    .getLockExpiration()
                    .isAfter(now)) {

                log.warn(
                        "Seat {} lock has expired",
                        seatId
                );


                seatReservationRepository.delete(
                        reservation
                );

                publishSeatEvent(
                        new SeatEvent(
                                SeatEvent.EventType.LOCK_EXPIRED,
                                event.getBookingId(),
                                event.getShowTimeId(),
                                event.getSeatIds(),
                                event.getKeycloakUserId()
                        )
                );

                throw new SeatUnavailableException(
                        "Seat " + seatId
                                + " lock has expired."
                );
            }

            if (!seatLockRedisService.isOwnedBy(
                    seatId,
                    event.getShowTimeId(),
                    event.getBookingId()
            )) {

                log.warn(
                        "Redis lock is no longer owned by booking {} for seat {}",
                        event.getBookingId(),
                        seatId
                );

                throw new SeatUnavailableException(
                        "Seat " + seatId + " lock has expired."
                );
            }


            reservation.setStatus(
                    ReservationStatus.BOOKED
            );

            reservation.setLockExpiration(null);

            reservations.add(reservation);
        }


        seatReservationRepository.saveAll(
                reservations
        );

        for (UUID seatId : event.getSeatIds()) {

            seatLockRedisService.releaseLock(
                    event.getShowTimeId(),
                    seatId,
                    event.getBookingId()
            );
        }

        log.info(
                "Successfully booked {} seats for bookingId {}",
                reservations.size(),
                event.getBookingId()
        );




        publishSeatEvent(
                new SeatEvent(
                        SeatEvent.EventType.SEAT_BOOKED,
                        event.getBookingId(),
                        event.getShowTimeId(),
                        event.getSeatIds(),
                        event.getKeycloakUserId()
                )
        );
    }

    @Override
    @Transactional
    public void releaseSeat(BookEvent event) {

        log.info(
                "Releasing seats for bookingId {}",
                event.getBookingId()
        );

        List<SeatReservation> reservations =
                seatReservationRepository
                        .findByBookingId(
                                event.getBookingId()
                        );

        if (reservations.isEmpty()) {

            log.warn(
                    "No seat reservations found for bookingId {}",
                    event.getBookingId()
            );

            return;
        }

        List<SeatReservation> releasableReservations =
                reservations.stream()
                        .filter(reservation ->
                                reservation.getStatus() == ReservationStatus.LOCKED
                                        || reservation.getStatus() == ReservationStatus.BOOKED)
                        .collect(Collectors.toList());
        if (!releasableReservations.isEmpty()) {

            seatReservationRepository.deleteAll(
                    releasableReservations
            );

            for (SeatReservation reservation : releasableReservations) {

                if (reservation.getStatus() == ReservationStatus.LOCKED) {
                    seatLockRedisService.releaseLock(
                            reservation.getShowtimeId(),
                            reservation.getSeatId(),
                            event.getBookingId()
                    );
                }
            }

            List<UUID> releasedSeatIds =
                    releasableReservations.stream()
                            .map(SeatReservation::getSeatId)
                            .toList();

            log.info(
                    "Released {} seats for bookingId {}",
                    releasableReservations.size(),
                    event.getBookingId()
            );

            publishSeatEvent(
                    new SeatEvent(
                            SeatEvent.EventType.SEAT_RELEASED,
                            event.getBookingId(),
                            event.getShowTimeId(),
                            releasedSeatIds,
                            event.getKeycloakUserId()
                    )
            );

        }



    }

    private void publishSeatEvent(SeatEvent event) {

        CompletableFuture<SendResult<String, SeatEvent>> future =
                seatKafkaProducer.publish(event);

        future.thenAccept(result ->
                log.info(
                        "Successfully published seat event {} for booking {}",
                        event.getEventType(),
                        event.getBookingId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish seat event {} for booking {}",
                    event.getEventType(),
                    event.getBookingId(),
                    ex
            );

            return null;
        });
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