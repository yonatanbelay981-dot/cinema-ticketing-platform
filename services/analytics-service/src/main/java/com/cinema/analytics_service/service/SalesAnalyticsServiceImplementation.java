package com.cinema.analytics_service.service;


import com.cinema.analytics_service.dto.SalesAnalyticResponse;
import com.cinema.analytics_service.entity.SalesAnalytics;
import com.cinema.analytics_service.exception.SalesAnalyticsNotFoundException;
import com.cinema.analytics_service.repository.SalesAnalyticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class SalesAnalyticsServiceImplementation
        implements SalesAnalyticsService {

    private final SalesAnalyticsRepository salesAnalyticsRepository;

    public SalesAnalyticsServiceImplementation(
            SalesAnalyticsRepository salesAnalyticsRepository
    ) {
        this.salesAnalyticsRepository = salesAnalyticsRepository;
    }

    @Override
    public SalesAnalyticResponse getSalesAnalyticsByDate(
            LocalDate date
    ) {

        log.info(
                "Fetching sales analytics for date {}",
                date
        );

        SalesAnalytics salesAnalytics =
                salesAnalyticsRepository.findByDate(date)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Sales analytics not found for date {}",
                                    date
                            );

                            return new SalesAnalyticsNotFoundException(
                                    "Sales analytics not found for date: " + date
                            );
                        });

        return mapToResponse(salesAnalytics);
    }

    @Override
    public Page<SalesAnalyticResponse> getAllSalesAnalytics(
            Pageable pageable
    ) {

        log.info("Fetching all sales analytics");

        return salesAnalyticsRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    private SalesAnalyticResponse mapToResponse(
            SalesAnalytics salesAnalytics
    ) {

        SalesAnalyticResponse response =
                new SalesAnalyticResponse();

        response.setDate(salesAnalytics.getDate());
        response.setTotalRevenue(
                salesAnalytics.getTotalRevenue()
        );
        response.setTotalTicketsSold(
                salesAnalytics.getTotalTicketsSold()
        );
        response.setTotalBookings(
                salesAnalytics.getTotalBookings()
        );

        return response;
    }
}
