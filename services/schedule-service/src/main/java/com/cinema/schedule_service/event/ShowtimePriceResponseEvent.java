package com.cinema.schedule_service.event;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowtimePriceResponseEvent {

    private UUID bookingId;
    private UUID showtimeId;
    private UUID movieId;
    private BigDecimal basePrice;
}
