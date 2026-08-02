package com.cinema.seat_service.dto;

import com.cinema.seat_service.entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {

    private UUID id;

    private UUID hallId;

    private String rowName;

    private Integer seatNumber;

    private SeatType seatType;
}
