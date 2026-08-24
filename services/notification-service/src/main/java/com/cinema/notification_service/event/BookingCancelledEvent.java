package com.cinema.notification_service.event;

import java.util.UUID;

public record BookingCancelledEvent(
        UUID eventId,
        UUID bookingId,
        UUID userId
) {
}
