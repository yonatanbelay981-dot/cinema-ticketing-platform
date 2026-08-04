package com.cinema.Booking_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
   @NotNull(message = "User ID is required")
    private UUID userId;
   @NotNull(message = "Showtime ID is required")
    private UUID showtimeId;
   @NotNull(message = "Seat IDs are required")
    private List<UUID> seatIds;
    private String promotionCode;
}
