package com.cinema.Booking_service.dto;

import com.cinema.Booking_service.entity.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
public class BookingResponse {

    private UUID id;

    private String keycloakUserId;

    private UUID showtimeId;

    private List<UUID> seatIds;

    private BigDecimal totalPrice;

    private BookingStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}