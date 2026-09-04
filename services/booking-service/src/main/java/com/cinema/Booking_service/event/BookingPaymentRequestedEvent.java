package com.cinema.Booking_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingPaymentRequestedEvent(
        UUID paymentRequestEventId,
        UUID bookingId,
        String keycloakUserId,
        BigDecimal totalPrice,
        String paymentMethod
) {
}
