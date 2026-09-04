package com.cinema.Booking_service.repository;

import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Page<Booking> findByKeycloakUserId(String keycloakUserId, Pageable pageable);
    Page<Booking> findByShowtimeId(UUID showtimeId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    Page<Booking> findByKeycloakUserIdAndStatus(String keycloakUserId, BookingStatus status, Pageable pageable);
    Optional<Booking> findByIdAndKeycloakUserId(UUID id, String keycloakUserId);
    @Query("""
    SELECT DISTINCT b
    FROM Booking b
    LEFT JOIN FETCH b.bookingSeats
    WHERE b.id = :id
""")
    Optional<Booking> findByIdWithBookingSeats(UUID id);

}
