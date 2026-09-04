package com.cinema.seat_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LockSeatsRequest {
    @NotNull(message = "showtimeId is required")
    private UUID showtimeId;

    @NotNull(message = "userId is required")
    private UUID userId;

    @NotEmpty(message = "seatIds list cannot be empty")
    private List<UUID> seatIds;

    @NotEmpty(message = "totalPrice can not be empty")
    private BigDecimal totalPrice;
}