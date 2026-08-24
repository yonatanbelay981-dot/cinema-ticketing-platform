package com.cinema.notification_service.service;

import com.cinema.notification_service.client.UserServiceClient;
import com.cinema.notification_service.dto.UserResponse;
import com.cinema.notification_service.event.BookingConfirmedEvent;
import com.cinema.notification_service.event.EmailNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class BookingConfirmedListener {
    private final UserServiceClient userServiceClient;
    private final EmailNotificationProducer emailNotificationProducer;

    public BookingConfirmedListener(UserServiceClient userServiceClient, EmailNotificationProducer emailNotificationProducer) {
        this.userServiceClient = userServiceClient;
        this.emailNotificationProducer = emailNotificationProducer;
    }

    @KafkaListener(
            topics = "booking-confirmed",
            containerFactory = "bookingConfirmedEventKafkaListenerContainerFactory"
    )
    public void listen(BookingConfirmedEvent event) {
        log.info(
                "Received BOOKING_CONFIRMED event. bookingId={}, userId={}",
                event.bookingId(),
                event.userId()
        );
        UserResponse user = userServiceClient.getUserById(event.userId());
        log.info(
                "Retrieved user details. userId={}, email={}",
                user.id(),
                user.email()
        );

        EmailNotificationMessage message =
                new EmailNotificationMessage(
                        UUID.randomUUID(),
                        user.id(),
                        user.email(),
                        "Booking Confirmed",
                        "Your cinema booking has been confirmed."
                );
        emailNotificationProducer.sendEmailNotification(message);

        log.info(
                "Booking confirmation email queued. bookingId={}",
                event.bookingId()
        );
    }
}
