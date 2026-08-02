package com.cinema.seat_service.dto;

import com.cinema.seat_service.entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeSeatResponse {
    private UUID seatId;
    private UUID hallId;
    private String rowName;
    private Integer seatNumber;
    private SeatType seatType;
    private String status;
}