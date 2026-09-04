package com.cinema.tiecket_service.service;


import com.cinema.tiecket_service.event.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketKafkaPaymentListener {

    private final TicketService ticketService;


    public TicketKafkaPaymentListener(TicketService ticketService) {
        this.ticketService = ticketService;

    }

    @KafkaListener(topics = "payment-events", groupId = "ticket-service" ,  containerFactory = "paymentProcessedEventConcurrentKafkaListenerContainerFactory")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info(
                "Received PAYMENT_PROCESSED event for booking {} with status {}",
                event.bookingId(),
                event.status()
        );

        if (!"SUCCESS".equals(event.status())) {

            log.info(
                    "Payment for booking {} was not successful. No ticket will be created.",
                    event.bookingId()
            );

            return;
        }

        ticketService.createTicketFromPayment(event);
        log.info(
                "Ticket creation triggered for booking {}",
                event.bookingId()
        );
    }

}
