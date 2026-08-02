package com.cinema.seat_service.controller;

import com.cinema.seat_service.dto.ApiResponse;
import com.cinema.seat_service.dto.SeatResponse;
import com.cinema.seat_service.dto.UpdateSeatRequest;
import com.cinema.seat_service.service.SeatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SeatResponse>>> getAllSeatsByHallId(@RequestParam UUID hallId, @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<SeatResponse> seats = seatService.getAllSeatsByHallId(hallId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seats retrieved successfully", seats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResponse>> getSeatsById(@PathVariable UUID id) {
        SeatResponse seat = seatService.getSeatsById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seat retrieved successfully", seat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResponse>> updateSeat(@PathVariable UUID id, @RequestBody UpdateSeatRequest request) {
        SeatResponse seat = seatService.updateSeat(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seat updated successfully", seat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(@PathVariable UUID id) {
        seatService.deleteSeatById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seat deleted successfully", null));
    }
}
