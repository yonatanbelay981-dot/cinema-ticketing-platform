package com.cinema.analytics_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieAnalyticResponse {

    private UUID movieId;
    private Long totalBookings;
    private Long ticketsSold;
    private BigDecimal totalRevenue;
}

