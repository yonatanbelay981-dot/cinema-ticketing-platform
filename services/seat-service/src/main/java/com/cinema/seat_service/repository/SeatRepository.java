package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SeatRepository  extends JpaRepository<Seat, UUID> {

    Page<Seat> findByHallId(UUID hallId, Pageable pageable);
    Optional<Seat> findByHallIdAndRowNameAndSeatNumber(UUID hallId, String rowName, Integer seatNumber);
    void deleteByHallId(UUID hallId);
    boolean existsByHallIdAndRowNameAndSeatNumber(UUID hallId, String rowName, Integer seatNumber);
    Page<Seat>  findByShowTime(UUID showtimeId , Pageable pageable);

}
