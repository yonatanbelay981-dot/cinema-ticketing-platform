package com.cinema.seat_service.service;

import com.cinema.seat_service.dto.SeatResponse;
import com.cinema.seat_service.dto.UpdateSeatRequest;
import com.cinema.seat_service.entity.Seat;
import com.cinema.seat_service.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class SeatServiceImplementation implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImplementation(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Page<SeatResponse> getAllSeatsByHallId(UUID hallId, Pageable pageable) {
        log.info("Fetching seats for hall with id {}" , hallId);
        Page<Seat> seats = seatRepository.findByHallId(hallId, pageable);
        log.info("Fetched seats for hall with id {} successfully" , hallId);
        return seats.map(this::convertToResponse);

    }

    @Override
    public SeatResponse getSeatsById(UUID id) {
        log.info("Fetching seat with id {}" , id);
        Seat seat = seatRepository.findById(id).orElseThrow(() -> {
            log.warn("Seat with id {} was not found" , id);
            return new RuntimeException("Seat not found with id " + id);
        });
        log.info("Fetched seat with id {} successfully" , id);
        return convertToResponse(seat);
    }

    @Override
    public SeatResponse updateSeat(UUID id, UpdateSeatRequest request) {
        log.info("Updating seat with id {}" , id);
        Seat seat = seatRepository.findById(id).orElseThrow(() -> {
            log.warn("while updating Seat with id {} was not found" , id);
            return new RuntimeException("Seat not found with id " + id);
        });
        // Update seat properties based on the request
       seat.setSeatType(request.getSeatType());
        Seat updatedSeat = seatRepository.save(seat);
        log.info("Updated seat with id {} successfully" , id);
        return convertToResponse(updatedSeat);

    }

    @Override
    public void deleteSeatById(UUID id) {
        log.info("Deleting seat with id {}" , id);
        seatRepository.deleteById(id);
        log.info("Deleted seat with id {} successfully" , id);
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
