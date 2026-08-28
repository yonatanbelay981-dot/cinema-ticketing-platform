package com.cinema.analytics_service.service;


import com.cinema.analytics_service.dto.BookingStatisticsResponse;
import com.cinema.analytics_service.entity.BookingAnalytics;
import com.cinema.analytics_service.repository.BookingAnalyticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class BookingAnalyticsServiceImplementation
        implements BookingAnalyticsService {

    private final BookingAnalyticsRepository bookingAnalyticsRepository;

    public BookingAnalyticsServiceImplementation(
            BookingAnalyticsRepository bookingAnalyticsRepository
    ) {
        this.bookingAnalyticsRepository = bookingAnalyticsRepository;
    }

    @Override
    public BookingStatisticsResponse getTodayAnalytics() {

        LocalDate today = LocalDate.now();

        log.info(
                "Fetching booking analytics for {}",
                today
        );

        BookingAnalytics analytics =
                bookingAnalyticsRepository
                        .findByDate(today)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking analytics not found for " + today
                                )
                        );

        return mapToResponse(analytics);
    }

    @Override
    public Page<BookingStatisticsResponse> getAllAnalytics(
            Pageable pageable
    ) {

        log.info("Fetching all booking analytics");

        return bookingAnalyticsRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    private BookingStatisticsResponse mapToResponse(
            BookingAnalytics analytics
    ) {

        BookingStatisticsResponse response =
                new BookingStatisticsResponse();

        response.setDate(analytics.getDate());
        response.setTotalBookings(analytics.getTotalBookings());
        response.setConfirmedBookings(
                analytics.getConfirmedBookings()
        );
        response.setFailedBookings(
                analytics.getFailedBookings()
        );
        response.setCancelledBookings(
                analytics.getCancelledBookings()
        );

        return response;
    }
}
