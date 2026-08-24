package com.cinema.concession_service.event;



import java.util.UUID;

public record FoodOrderCancelledEvent(
        UUID eventId,
        UUID foodOrderId,
        UUID bookingId
) {
}
