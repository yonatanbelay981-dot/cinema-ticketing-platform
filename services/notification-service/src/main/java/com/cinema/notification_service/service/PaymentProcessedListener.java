package com.cinema.notification_service.service;

import com.cinema.notification_service.client.BookingServiceClient;
import com.cinema.notification_service.client.UserServiceClient;
import com.cinema.notification_service.dto.BookingResponse;
import com.cinema.notification_service.dto.UserResponse;
import com.cinema.notification_service.entity.PaymentStatus;
import com.cinema.notification_service.event.EmailNotificationMessage;
import com.cinema.notification_service.event.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class PaymentProcessedListener {

    private final BookingServiceClient bookingServiceClient;
    private final UserServiceClient userServiceClient;
    private final EmailNotificationProducer emailNotificationProducer;

    public PaymentProcessedListener(
            BookingServiceClient bookingServiceClient,
            UserServiceClient userServiceClient,
            EmailNotificationProducer emailNotificationProducer
    ) {
        this.bookingServiceClient = bookingServiceClient;
        this.userServiceClient = userServiceClient;
        this.emailNotificationProducer = emailNotificationProducer;
    }

    @KafkaListener(
            topics = "payment-processed",
            groupId = "notification-service-payment",
            containerFactory = "paymentProcessedEventKafkaListenerContainerFactory"
    )
    public void listenPaymentProcessed(
            PaymentProcessedEvent event
    ) {

        log.info(
                "Received PAYMENT_PROCESSED event. paymentId={}, bookingId={}, status={}",
                event.paymentId(),
                event.bookingId(),
                event.status()
        );

        BookingResponse booking =
                bookingServiceClient.getBookingById(
                        event.bookingId()
                );

        log.info(
                "Retrieved booking details. bookingId={}, userId={}",
                booking.id(),
                booking.userId()
        );


        UserResponse user =
                userServiceClient.getUserById(
                        booking.userId()
                );

        log.info(
                "Retrieved user details. userId={}, email={}",
                user.id(),
                user.email()
        );


        String subject;
        String message;

        if (event.status() == PaymentStatus.SUCCESS) {

            subject = "Payment Successful";

            message =
                    "Your payment for the cinema booking has been processed successfully.";

        } else if (event.status() == PaymentStatus.FAILED) {

            subject = "Payment Failed";

            message =
                    "Your payment for the cinema booking has failed. "
                            + "Please try again.";

        } else {

            log.warn(
                    "Unknown payment status {} for payment {}",
                    event.status(),
                    event.paymentId()
            );

            return;
        }


        EmailNotificationMessage notification =
                new EmailNotificationMessage(
                        UUID.randomUUID(),
                        user.id(),
                        user.email(),
                        subject,
                        message
                );


        emailNotificationProducer.sendEmailNotification(
                notification
        );

        log.info(
                "Payment notification queued successfully. paymentId={}, bookingId={}, email={}",
                event.paymentId(),
                event.bookingId(),
                user.email()
        );
    }
}