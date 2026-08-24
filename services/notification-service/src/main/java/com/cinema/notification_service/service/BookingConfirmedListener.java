package com.cinema.notification_service.service;

import com.cinema.notification_service.client.UserServiceClient;
import com.cinema.notification_service.dto.UserResponse;
import com.cinema.notification_service.event.BookingConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingConfirmedListener {
    private final UserServiceClient userServiceClient;

    public BookingConfirmedListener(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
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

    }
}
