package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.SeatReservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, UUID> {
    List<SeatReservation> findActiveReservations(UUID showtimeId  , LocalDateTime now);
}
