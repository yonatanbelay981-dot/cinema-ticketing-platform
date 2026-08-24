package com.cinema.notification_service.client;

import com.cinema.notification_service.dto.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "booking-service",
        url = "${app.services.booking-service.url}"
)
public interface BookingServiceClient {
    @GetMapping("api/bookings/{bookingId}")
    BookingResponse getBookingById(@PathVariable UUID bookingId);

}
