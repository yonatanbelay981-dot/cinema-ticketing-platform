package com.cinema.seat_service.service;

import com.cinema.seat_service.event.BookEvent;
import com.cinema.seat_service.exception.SeatUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class SeatBookingEventListener {

    private final SeatService seatService;
    public SeatBookingEventListener(SeatService seatService)
    {
        this.seatService = seatService;

    }

    @KafkaListener( topics = "booking-event-topic", groupId = "seat-service", containerFactory = "bookEventConcurrentKafkaListenerContainerFactory")
    public void listenBookingEvent(BookEvent bookEvent) {

        log.info( "Received booking event: {} for booking: {}", bookEvent.getEventType(), bookEvent.getBookingId() );



        if (bookEvent.getEventType() == null) {
            log.error(
                    "Ignoring malformed booking event for booking {}: eventType is null",
                    bookEvent.getBookingId()
            );
            return;
        }

        switch (bookEvent.getEventType()) {

            case LOCK_SEATS_REQUESTED:

                lockSeats(bookEvent);

                break;

            case RELEASE_SEAT_REQUESTED:

                releaseSeats(bookEvent);

                break;

            case BOOKING_SEAT_REQUESTED:

                bookSeats(bookEvent);

                break;

            default: log.warn( "Unknown booking event type: {}", bookEvent.getEventType() );

        }

    }
    private void bookSeats(BookEvent bookEvent) {

        log.info(
                "Booking seats permanently for booking {}",
                bookEvent.getBookingId()
        );

        try {

            seatService.bookSeats(bookEvent);

        } catch (SeatUnavailableException e) {

            log.warn(
                    "Could not book seats for booking {}: {}. " +
                            "The event will be considered handled.",
                    bookEvent.getBookingId(),
                    e.getMessage()
            );

        }
    }
    private void releaseSeats(BookEvent bookEvent) {

        log.info( "Releasing seats for booking {}", bookEvent.getBookingId() );

        seatService.releaseSeat(bookEvent);
    }

    private void lockSeats(BookEvent bookEvent) {
        log.info( "Locking seats for booking {}", bookEvent.getBookingId() );
        try {

            seatService.lockSeatsForCheckout(bookEvent);

        }catch (SeatUnavailableException e){
            log.warn(
                    "Could not lock seats for booking {}: {}",
                    bookEvent.getBookingId(),
                    e.getMessage()
            );

        }


    }

}
