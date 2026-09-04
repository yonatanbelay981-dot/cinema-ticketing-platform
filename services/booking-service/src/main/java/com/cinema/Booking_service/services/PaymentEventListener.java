package com.cinema.Booking_service.services;

import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingSeat;
import com.cinema.Booking_service.entity.BookingStatus;
import com.cinema.Booking_service.event.BookingSeatRequestedEvent;
import com.cinema.Booking_service.event.PaymentProcessedEvent;
import com.cinema.Booking_service.event.ReleaseSeatRequestedEvent;
import com.cinema.Booking_service.exception.BookingNotFoundException;
import com.cinema.Booking_service.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.support.SendResult;


import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentEventListener {
    private final BookingRepository bookingRepository;
    private final KafkaBookingProducer  kafkaBookingProducer;



    public PaymentEventListener(BookingRepository bookingRepository, KafkaBookingProducer kafkaBookingProducer) {
        this.bookingRepository = bookingRepository;
        this.kafkaBookingProducer = kafkaBookingProducer;

    }
    @KafkaListener(
            topics = "payment-events",
            groupId = "booking-service",
            containerFactory = "paymentProcessedKafkaListenerContainerFactory"
    )
    public void handlePaymentProcessed(
            PaymentProcessedEvent event
    ){
        log.info(
                "Received payment event for booking {} with status {}",
                event.bookingId(),
                event.status()
        );
        Booking booking  =  bookingRepository.findByIdWithBookingSeats(event.bookingId()).orElseThrow(()->{
            log.warn(
                    "Booking not found with id {}",
                    event.bookingId()
            );
            return new BookingNotFoundException( "Booking not found: " + event.bookingId());


        });

        if(booking.getStatus()!= BookingStatus.LOCKED){
            log.warn(
                    "Ignoring payment event for booking {} because " +
                            "booking status is {}",
                    booking.getId(),
                    booking.getStatus()
            );
           return;
        }
        if("SUCCESS".equals(event.status())){

            log.info(
                    "Booking {} Locked  successfully after payment",
                    booking.getId()
            );

            BookingSeatRequestedEvent bookingSeatRequestedEvent =
                    new BookingSeatRequestedEvent(
                            booking.getId(),
                            BookingSeatRequestedEvent.EventType.BOOKING_SEAT_REQUESTED,
                           booking.getShowtimeId(),
                            booking.getBookingSeats()
                                    .stream()
                                    .map(BookingSeat::getSeatId)
                                    .toList(),
                            booking.getKeycloakUserId()
                    );

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaBookingProducer.publish(
                            booking.getId(),
                            bookingSeatRequestedEvent
                    );

            future.thenAccept(result ->
                    log.info(
                            "BOOKING_SEAT_REQUESTED published successfully for bookingId: {}",
                            booking.getId()
                    )
            ).exceptionally(ex -> {

                log.error(
                        "Failed to publish BOOKING_SEAT_REQUESTED for bookingId: {}",
                        booking.getId(),
                        ex
                );

                return null;
            });


        }else{
            log.warn(
                    "Payment failed for booking {}",
                    booking.getId()
            );

            booking.setStatus(BookingStatus.FAILED);

            bookingRepository.save(booking);
            ReleaseSeatRequestedEvent releaseEvent =
                    new ReleaseSeatRequestedEvent(
                            ReleaseSeatRequestedEvent.EventType.RELEASE_SEAT_REQUESTED,
                            booking.getId(),
                            booking.getShowtimeId(),
                            booking.getBookingSeats()
                                    .stream()
                                    .map(BookingSeat::getSeatId)
                                    .toList(),
                            booking.getKeycloakUserId()
                    );

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaBookingProducer.publish(
                            booking.getId(),
                            releaseEvent
                    );

            future.thenAccept(result ->
                    log.info(
                            "RELEASE_SEAT_REQUESTED published successfully for bookingId: {}",
                            booking.getId()
                    )
            ).exceptionally(ex -> {

                log.error(
                        "Failed to publish RELEASE_SEAT_REQUESTED for bookingId: {}",
                        booking.getId(),
                        ex
                );

                return null;
            });
        }
    }
}
