package com.cinema.analytics_service.service;



import com.cinema.analytics_service.dto.BookingStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingAnalyticsService {

    BookingStatisticsResponse getTodayAnalytics();

    Page<BookingStatisticsResponse> getAllAnalytics(
            Pageable pageable
    );
}
