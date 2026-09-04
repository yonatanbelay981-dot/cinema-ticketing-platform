package com.cinema.Booking_service.event;

import java.util.UUID;

public record BookingCancelledEvent(
        UUID eventId,
        UUID bookingId,
        String keycloakUserId
) {
}
