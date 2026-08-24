package com.cinema.concession_service.event;


import java.util.UUID;

public record BookingConfirmedEvent(
        UUID eventId,
        UUID bookingId,
        UUID userId
) {
}
