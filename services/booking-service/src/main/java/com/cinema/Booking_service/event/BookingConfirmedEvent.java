package com.cinema.Booking_service.event;

import java.util.UUID;

public record BookingConfirmedEvent(
        UUID eventId,
        UUID bookingId,
        String keycloakUserId
) {
}