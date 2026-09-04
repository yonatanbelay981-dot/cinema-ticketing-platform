package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.ReservationStatus;
import com.cinema.seat_service.entity.SeatReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  SeatReservationRepository extends JpaRepository<SeatReservation, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatReservation> findByShowtimeIdAndStatusInAndLockExpirationAfter(UUID showtimeId  ,    List<ReservationStatus> statuses  , LocalDateTime now);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatReservation> findBySeatIdAndShowtimeIdAndBookingIdAndStatus(
            UUID seatId,
            UUID showtimeId,
            UUID bookingId,
            ReservationStatus status
    );

    List<SeatReservation> findByBookingId(UUID bookingId);
    List<SeatReservation> findByShowtimeIdAndStatusIn(
            UUID showtimeId,
            List<ReservationStatus> statuses
    );
    Optional<SeatReservation> findByShowtimeIdAndSeatIdAndStatusIn(
            UUID showtimeId,
            UUID seatId,
            List<ReservationStatus> statuses

    );
}
