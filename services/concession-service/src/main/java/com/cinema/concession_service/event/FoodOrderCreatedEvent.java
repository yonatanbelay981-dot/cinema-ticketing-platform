package com.cinema.concession_service.event;


import com.cinema.concession_service.entity.FoodOrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record FoodOrderCreatedEvent(
        UUID eventId,
        UUID foodOrderId,
        UUID bookingId,
        UUID userId,
        BigDecimal totalPrice,
        FoodOrderStatus status
) {
}
