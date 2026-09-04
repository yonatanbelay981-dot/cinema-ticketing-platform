package com.cinema.Booking_service.event;

import java.math.BigDecimal;
import java.util.UUID;


public record PaymentProcessedEvent(

        UUID paymentId,
        UUID bookingId,
        String keycloakUserId,
        BigDecimal amount,
        String status,
        String transactionReference
) {
}