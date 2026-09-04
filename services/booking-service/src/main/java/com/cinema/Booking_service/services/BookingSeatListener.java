package com.cinema.Booking_service.services;

import com.cinema.Booking_service.entity.Booking;
import com.cinema.Booking_service.entity.BookingStatus;
import com.cinema.Booking_service.event.BookingConfirmedEvent;
import com.cinema.Booking_service.event.BookingPaymentRequestedEvent;
import com.cinema.Booking_service.event.SeatEvent;
import com.cinema.Booking_service.event.ShowtimePriceResponseEvent;
import com.cinema.Booking_service.exception.BookingNotFoundException;
import com.cinema.Booking_service.repository.BookingRepository;
import com.cinema.common_lib.event.BookingAnalyticsEvent;
import com.cinema.common_lib.event.BookingAnalyticsStatus;
import com.cinema.common_lib.event.BookingStatusAnalyticsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BookingSeatListener {

    private final BookingRepository bookingRepository;
    private final KafkaBookingProducer kafkaBookingProducer;


    public BookingSeatListener(
            BookingRepository bookingRepository,
            KafkaBookingProducer kafkaBookingProducer
    ) {
        this.bookingRepository = bookingRepository;
        this.kafkaBookingProducer = kafkaBookingProducer;

    }

    @KafkaListener(
            topics = "seat-event-topic",
            groupId = "booking-service",
            containerFactory = "seatKafkaListenerContainerFactory"
    )
    public void listenSeatEvents(SeatEvent event) {

        log.info(
                "Received seat event: {} for booking: {}",
                event.getEventType(),
                event.getBookingId()
        );

        switch (event.getEventType()) {

            case SEAT_LOCKED:
                handleSeatLocked(event);
                break;

            case LOCK_FAILED:
                handleLockFailed(event);
                break;

            case SEAT_BOOKED:
                handleBookedSeat(event);
                break;

            case SEAT_RELEASED:
                log.info(
                        "Seats released for booking: {}",
                        event.getBookingId()
                );
                break;

            case LOCK_EXPIRED:
                handleLockExpired(event);
                break;

            default:
                log.warn(
                        "Unknown seat event type: {}",
                        event.getEventType()
                );
        }
    }

    private void handleSeatLocked(SeatEvent event) {

        Booking booking = findBooking(event);

        if (booking.getStatus() != BookingStatus.PENDING) {

            log.warn(
                    "Ignoring SEAT_LOCKED for booking {} because status is {}",
                    booking.getId(),
                    booking.getStatus()
            );

            return;
        }

        booking.setStatus(BookingStatus.LOCKED);

        bookingRepository.save(booking);

        log.info(
                "Booking {} changed from PENDING to LOCKED",
                booking.getId()
        );



        BookingPaymentRequestedEvent paymentEvent =
                new BookingPaymentRequestedEvent(
                        UUID.randomUUID(),
                        booking.getId(),
                        booking.getKeycloakUserId(),
                        booking.getTotalPrice(),
                        booking.getPaymentMethod()
                );
        log.info(
                "Publishing payment event: paymentRequestEventId={}, bookingId={}, userId={}, amount={}, method={}",
                paymentEvent.paymentRequestEventId(),
                paymentEvent.bookingId(),
                paymentEvent.keycloakUserId(),
                paymentEvent.totalPrice(),
                paymentEvent.paymentMethod()
        );


        CompletableFuture<SendResult<String, Object>> future =
                kafkaBookingProducer.publishBookingPayment(
                        booking.getId(),
                        paymentEvent
                );

        future.thenAccept(result ->
                log.info(
                        "BOOKING_PAYMENT_REQUESTED published successfully for bookingId: {}",
                        booking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_PAYMENT_REQUESTED for bookingId: {}",
                    booking.getId(),
                    ex
            );

            return null;
        });

    }


    private void handleBookedSeat(SeatEvent event) {

        Booking booking = findBooking(event);

        if (booking.getStatus() != BookingStatus.LOCKED) {

            log.warn(
                    "Ignoring SEAT_BOOKED for booking {} because status is {}",
                    booking.getId(),
                    booking.getStatus()
            );

            return;
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking {} changed from LOCKED to CONFIRMED",
                savedBooking.getId()
        );

        BookingConfirmedEvent confirmedEvent =
                new BookingConfirmedEvent(
                        UUID.randomUUID(),
                        savedBooking.getId(),
                        savedBooking.getKeycloakUserId()
                );

        CompletableFuture<SendResult<String, Object>> future =
                kafkaBookingProducer.publishBookingConfirmed(
                        savedBooking.getId(),
                        confirmedEvent
                );

        future.thenAccept(result ->
                log.info(
                        "BOOKING_CONFIRMED published successfully for bookingId: {}",
                        savedBooking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_CONFIRMED for bookingId: {}",
                    savedBooking.getId(),
                    ex
            );

            return null;
        });
        BookingAnalyticsEvent analyticsEvent =
                new BookingAnalyticsEvent(
                        UUID.randomUUID(),
                        savedBooking.getId(),
                        savedBooking.getKeycloakUserId(),
                        savedBooking.getShowtimeId(),
                        savedBooking.getMovieId(),
                        savedBooking.getBookingSeats().size(),
                        savedBooking.getTotalPrice()
                );
        CompletableFuture<SendResult<String, Object>> future1 =
                kafkaBookingProducer.publishBookingAnalyticsEvent(
                        savedBooking.getId(),
                        analyticsEvent
                );

        future1.thenAccept(result ->
                log.info(
                        "BOOKING_ANALYTICS_EVENT published successfully for bookingId: {}",
                        savedBooking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_ANALYTICS_EVENT for bookingId: {}",
                    savedBooking.getId(),
                    ex
            );

            return null;
        });

        BookingStatusAnalyticsEvent statusAnalyticsEvent =
                new BookingStatusAnalyticsEvent(
                        UUID.randomUUID(),
                        savedBooking.getId(),
                        savedBooking.getKeycloakUserId(),
                        savedBooking.getShowtimeId(),
                        savedBooking.getMovieId(),
                        BookingAnalyticsStatus.CONFIRMED
                );
        CompletableFuture<SendResult<String, Object>> future3 =
                kafkaBookingProducer.publishBookingStatusAndAnalyticsEvent(
                        savedBooking.getId(),
                        statusAnalyticsEvent
                );

        future1.thenAccept(result ->
                log.info(
                        "BOOKING-STATUS_ANALYTICS_EVENT published successfully for bookingId: {}",
                        savedBooking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_ANALYTICS_EVENT for bookingId: {}",
                    savedBooking.getId(),
                    ex
            );

            return null;
        });
    }

    private void handleLockExpired(SeatEvent event) {

        Booking booking = findBooking(event);

        if (booking.getStatus() != BookingStatus.LOCKED) {

            log.warn(
                    "Ignoring LOCK_EXPIRED for booking {} because status is {}",
                    booking.getId(),
                    booking.getStatus()
            );

            return;
        }

        booking.setStatus(BookingStatus.EXPIRED);
        bookingRepository.save(booking);

        log.info(
                "Booking {} changed to EXPIRED",
                booking.getId()
        );
    }

    private void handleLockFailed(SeatEvent event) {

        Booking booking = findBooking(event);

        if (booking.getStatus() != BookingStatus.PENDING) {

            log.warn(
                    "Ignoring LOCK_FAILED for booking {} because status is {}",
                    booking.getId(),
                    booking.getStatus()
            );

            return;
        }

        booking.setStatus(BookingStatus.FAILED);
        bookingRepository.save(booking);

        log.info(
                "Booking {} changed to FAILED",
                booking.getId()
        );

        BookingStatusAnalyticsEvent statusAnalyticsEvent =
                new BookingStatusAnalyticsEvent(
                        UUID.randomUUID(),
                        booking.getId(),
                        booking.getKeycloakUserId(),
                        booking.getShowtimeId(),
                        booking.getMovieId(),
                        BookingAnalyticsStatus.FAILED
                );
        CompletableFuture<SendResult<String, Object>> future =
                kafkaBookingProducer.publishBookingStatusAndAnalyticsEvent(
                        booking.getId(),
                        statusAnalyticsEvent
                );

        future.thenAccept(result ->
                log.info(
                        "BOOKING-STATUS_ANALYTICS_EVENT for failed event  published successfully for bookingId: {}",
                        booking.getId()
                )
        ).exceptionally(ex -> {

            log.error(
                    "Failed to publish BOOKING_ANALYTICS_EVENT for bookingId: {}",
                    booking.getId(),
                    ex
            );

            return null;
        });

    }

    private Booking findBooking(SeatEvent event) {

        return bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> {

                    log.warn(
                            "Booking not found with Id: {}",
                            event.getBookingId()
                    );

                    return new BookingNotFoundException(
                            "Booking not found"
                    );
                });
    }


}