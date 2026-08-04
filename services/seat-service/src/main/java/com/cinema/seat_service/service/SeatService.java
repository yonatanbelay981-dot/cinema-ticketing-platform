package com.cinema.seat_service.service;

import com.cinema.seat_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatService {
    Page<SeatResponse> getAllSeatsByHallId(UUID hallId, Pageable pageable);
    Optional<SeatResponse> getByHallIdRowNameAndSeatNumber(UUID hallId, String rowName, Integer seatNumber);
    SeatResponse getSeatsById(UUID id);
    SeatResponse updateSeat(UUID id  , UpdateSeatRequest request);
    void  deleteSeatById(UUID id);
    List<ShowtimeSeatResponse> getSeatMapForShowtime(UUID hallId, UUID showtimeId);
    List<UUID> lockSeatsForCheckout(LockSeatsRequest request);
    void bookSeats(LockSeatsRequest request);

}
