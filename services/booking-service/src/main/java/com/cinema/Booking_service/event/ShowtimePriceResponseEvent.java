package com.cinema.Booking_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimePriceResponseEvent {

    private UUID bookingId;
    private UUID showtimeId;
    private UUID movieId;
    private BigDecimal basePrice;


}
