package com.cinema.Booking_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingConfirmedEvent(
        UUID bookingId,
        UUID userId,
        BigDecimal totalPrice,
        String paymentMethod
) {
}
