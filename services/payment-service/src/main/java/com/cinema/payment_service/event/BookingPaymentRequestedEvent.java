package com.cinema.payment_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingPaymentRequestedEvent(UUID bookingId,
                                           UUID eventId,
                                           UUID userId,
                                           BigDecimal totalPrice,
                                           String paymentMethod) {
}
