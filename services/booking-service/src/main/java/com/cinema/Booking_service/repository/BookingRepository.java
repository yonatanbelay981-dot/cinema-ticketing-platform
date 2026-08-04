package com.cinema.Booking_service.repository;

import com.cinema.Booking_service.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Page<Booking> findByUserId(UUID userId, Pageable pageable);
    Page<Booking> findByShowtimeId(UUID showtimeId, Pageable pageable);
    Page<Booking> findByStatus(UUID statusId , Pageable pageable);
    Page<Booking> findByUserIdAndStatus(UUID userId, UUID statusId , Pageable pageable);
    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);

}
