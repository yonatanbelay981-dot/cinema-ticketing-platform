package com.cinema.Booking_service.event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimePriceRequestedEvent {

    private UUID bookingId;
    private UUID showtimeId;
}
