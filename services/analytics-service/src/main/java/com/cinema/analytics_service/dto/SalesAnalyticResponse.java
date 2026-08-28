package com.cinema.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticResponse {

    private LocalDate date;
    private BigDecimal totalRevenue;
    private Long totalTicketsSold;
    private Long totalBookings;
}
