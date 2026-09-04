package com.cinema.Booking_service.services;

import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingSeat;
import com.cinema.Booking_service.entity.BookingStatus;
import com.cinema.Booking_service.event.LockSeatsRequestedEvent;
import com.cinema.Booking_service.event.ShowtimePriceResponseEvent;
import com.cinema.Booking_service.exception.BookingNotFoundException;
import com.cinema.Booking_service.repository.BookingRepository;
import com.cinema.Booking_service.repository.BookingSeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ShowtimePriceEventListener {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final KafkaBookingProducer kafkaBookingProducer;

    public ShowtimePriceEventListener(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            KafkaBookingProducer kafkaBookingProducer
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.kafkaBookingProducer = kafkaBookingProducer;
    }

    @KafkaListener(
            topics = "showtime-price-responses",
            groupId = "booking-service",
            containerFactory = "showTimeKafkaListenerContainerFactory"
    )
    public void handleShowtimePriceResponse(
            ShowtimePriceResponseEvent event
    ) {

        log.info(
                "Received showtime price response. bookingId={}, price={}",
                event.getBookingId(),
                event.getBasePrice()
        );

        Booking booking = bookingRepository
                .findById(event.getBookingId())
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found: " + event.getBookingId()
                        )
                );


        if (booking.getStatus() != BookingStatus.PENDING) {

            log.warn(
                    "Ignoring price response for booking {} because status is {}",
                    booking.getId(),
                    booking.getStatus()
            );

            return;
        }
        booking.setMovieId(event.getMovieId());

        log.info("Booking ID: {}", event.getBookingId());
        log.info("Base Price: {}", event.getBasePrice());
        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBookingId(booking.getId());

        log.info(
                "Booking {} has {} seats",
                booking.getId(),
                bookingSeats.size()
        );


        for (BookingSeat bookingSeat : bookingSeats) {

            bookingSeat.setPrice(event.getBasePrice());
        }

        bookingSeatRepository.saveAll(bookingSeats);

        log.info(
                "Booking {} has {} seats",
                booking.getId(),
                bookingSeats.size()
        );
        BigDecimal totalPrice =
                event.getBasePrice()
                        .multiply(
                                BigDecimal.valueOf(bookingSeats.size())
                        );

        booking.setTotalPrice(totalPrice);

        bookingRepository.save(booking);

        log.info(
                "Booking {} total price calculated: {}",
                booking.getId(),
                totalPrice
        );


        LockSeatsRequestedEvent lockEvent =
                new LockSeatsRequestedEvent(
                        booking.getId(),
                        booking.getShowtimeId(),
                        bookingSeats.stream()
                                .map(BookingSeat::getSeatId)
                                .toList(),
                        booking.getKeycloakUserId(),
                        LockSeatsRequestedEvent.EventType.LOCK_SEATS_REQUESTED
                );

        kafkaBookingProducer.publish(
                booking.getId(),
                lockEvent
        );

        log.info(
                "LOCK_SEATS_REQUESTED published for booking {}",
                booking.getId()
        );
    }
}