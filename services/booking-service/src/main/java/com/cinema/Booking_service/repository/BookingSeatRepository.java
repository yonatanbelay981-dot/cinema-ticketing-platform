package com.cinema.Booking_service.repository;

import com.cinema.Booking_service.entity.BookingSeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingSeatRepository extends JpaRepository<BookingSeat , UUID> {
    Page<BookingSeat>findByBookingId(UUID booking_id  , Pageable pageable);
    Optional<BookingSeat> findByBookingIdAndSeatId(UUID booking_id , UUID seat_id);
    void deleteByBookingId(UUID booking_id);
    long countByBookingId(UUID booking_id);
    List<BookingSeat> findBySeatId(UUID seatId);

}
