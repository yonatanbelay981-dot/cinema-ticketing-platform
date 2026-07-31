package com.cinema.schedule_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShowtimeRequest {
    @NotNull(message = "movieId is required")
    private UUID movieId;
    @NotNull(message = "hallId is required")
    private UUID hallId;
    @NotNull(message = "startTime is required")
    private LocalDateTime startTime;
    @NotNull(message = "endTime is required")
    private LocalDateTime endTime;
    @NotNull(message = "basePrice is required")
    @Positive(message = "basePrice must be positive")
    private BigDecimal basePrice;
}
