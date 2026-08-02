package com.cinema.seat_service.dto;

import com.cinema.seat_service.entity.SeatType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeatRequest {
    @NotNull(message = "seatType is required")
    private SeatType seatType;
}
