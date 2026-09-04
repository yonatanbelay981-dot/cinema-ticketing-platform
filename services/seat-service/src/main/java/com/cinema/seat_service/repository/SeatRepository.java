package com.cinema.seat_service.repository;

import com.cinema.seat_service.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository  extends JpaRepository<Seat, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdForUpdate(@Param("id") UUID id);
    List<Seat> findByHallId(UUID hallId);
    Optional<Seat> findByHallIdAndRowNameAndSeatNumber(UUID hallId, String rowName, Integer seatNumber);
    void deleteByHallId(UUID hallId);
    boolean existsByHallIdAndRowNameAndSeatNumber(UUID hallId, String rowName, Integer seatNumber);


}
