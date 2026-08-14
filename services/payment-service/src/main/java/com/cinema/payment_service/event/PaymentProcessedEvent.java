package com.cinema.payment_service.event;

import com.cinema.payment_service.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessedEvent(
        UUID paymentId,
        UUID bookingId,
        BigDecimal amount,
        PaymentStatus status,
        String transactionReference
) {
}
