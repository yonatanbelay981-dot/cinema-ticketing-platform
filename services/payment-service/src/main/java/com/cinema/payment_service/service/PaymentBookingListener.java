package com.cinema.payment_service.service;

import com.cinema.payment_service.entity.Payment;
import com.cinema.payment_service.event.BookingPaymentRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentBookingListener {
    private final PaymentService paymentService;

    public PaymentBookingListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "booking-payment-topic",
            groupId = "payment-service",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void handleBookingPaymentRequested (
            BookingPaymentRequestedEvent event
    ) throws InterruptedException {

        log.info(
                "Received payment request: eventId={}, bookingId={}, userId={}, amount={}, method={}",
                event.paymentRequestEventId(),
                event.bookingId(),
                event.keycloakUserId(),
                event.totalPrice(),
                event.paymentMethod()
        );

        Payment payment =
                paymentService.createPaymentFromBooking(event);


        paymentService.processPayment(payment.getId());

        log.info(
                "Payment processing completed for booking {}",
                event.bookingId()
        );
    }
}
