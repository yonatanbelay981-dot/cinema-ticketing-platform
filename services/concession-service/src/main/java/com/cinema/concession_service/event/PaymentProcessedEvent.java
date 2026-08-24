package com.cinema.concession_service.event;

import com.cinema.concession_service.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessedEvent(
        UUID paymentId,
        UUID bookingId,
        BigDecimal amount,
        PaymentStatus paymentStatus,
        String transactionReference
) {
}
