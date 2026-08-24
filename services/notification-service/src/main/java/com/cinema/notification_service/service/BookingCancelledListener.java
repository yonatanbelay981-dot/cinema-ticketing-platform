package com.cinema.notification_service.service;

import com.cinema.notification_service.client.UserServiceClient;
import com.cinema.notification_service.dto.UserResponse;
import com.cinema.notification_service.event.BookingCancelledEvent;
import com.cinema.notification_service.event.EmailNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
public class BookingCancelledListener {


        private final UserServiceClient userServiceClient;
        private final EmailNotificationProducer emailNotificationProducer;

        public BookingCancelledListener(UserServiceClient userServiceClient, EmailNotificationProducer emailNotificationProducer) {
            this.userServiceClient = userServiceClient;
            this.emailNotificationProducer = emailNotificationProducer;
        }

        @KafkaListener(
                topics = "booking-cancelled",
                containerFactory = "bookingCancelledEventKafkaListenerContainerFactory"
        )
        public void listen(BookingCancelledEvent event) {
            log.info(
                    "Received BOOKING_CANCELLED event. bookingId={}, userId={}",
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
                            "Booking Cancelled",
                            "Your cinema booking has been cancelled."
                    );
            emailNotificationProducer.sendEmailNotification(message);

            log.info(
                    "Booking cancellation email queued. bookingId={}",
                    event.bookingId()
            );
        }
    }


