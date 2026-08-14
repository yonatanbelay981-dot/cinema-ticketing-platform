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

    @KafkaListener(topics = "booking-events",
    groupId = "payment-service")
    public void handleBookingPaymentRequested( BookingPaymentRequestedEvent event){



        log.info(
                "Received payment request for booking {}",
                event.bookingId()
        );
       Payment payment =  paymentService.createPaymentFromBooking(event);

       paymentService.processPayment(payment.getId());


        log.info(
                "Payment processing completed for booking {}",
                event.bookingId()
        );


    }

}
