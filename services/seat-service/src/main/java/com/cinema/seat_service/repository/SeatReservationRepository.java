package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.SeatReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SeatReservation> findByShowtimeIdAndStatusInAndLockExpirationAfter(UUID showtimeId  , LocalDateTime now);
    Optional<SeatReservation> findLockedReservation(UUID showtimeId , UUID seatId);
}
