package com.cinema.concession_service.service;



import com.cinema.concession_service.event.BookingConfirmedEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingConfirmedListener {

    private final FoodOrderService foodOrderService;

    public BookingConfirmedListener(
            FoodOrderService foodOrderService
    ) {
        this.foodOrderService = foodOrderService;
    }

    @KafkaListener(
            topics = "booking-confirmed-topic",
            groupId = "concession-service",
            containerFactory =
                    "bookingConfirmedKafkaListenerContainerFactory"
    )
    public void handleBookingConfirmed(
            BookingConfirmedEvent event
    ) {

        log.info(
                "Received BOOKING_CONFIRMED event for booking {}",
                event.bookingId()
        );

        /*
         * For now, this listener is mainly demonstrating
         * communication from Booking Service to Concession Service.
         *
         * We will NOT create a food order here because
         * the food order is selected by the customer.
         */

        log.info(
                "Booking {} confirmed for user {}",
                event.bookingId(),
                event.userId()
        );
    }
}
