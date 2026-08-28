package com.cinema.analytics_service.controller;



import com.cinema.analytics_service.dto.BookingStatisticsResponse;
import com.cinema.analytics_service.service.BookingAnalyticsService;
import com.cinema.analytics_service.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/bookings")
public class BookingAnalyticsController {

    private final BookingAnalyticsService bookingAnalyticsService;

    public BookingAnalyticsController(
            BookingAnalyticsService bookingAnalyticsService
    ) {
        this.bookingAnalyticsService = bookingAnalyticsService;
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<BookingStatisticsResponse>> getTodayAnalytics() {

        BookingStatisticsResponse analytics =
                bookingAnalyticsService.getTodayAnalytics();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Today's booking analytics fetched successfully",
                        analytics
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookingStatisticsResponse>>> getAllAnalytics(
            Pageable pageable
    ) {

        Page<BookingStatisticsResponse> analytics =
                bookingAnalyticsService.getAllAnalytics(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking analytics fetched successfully",
                        analytics
                )
        );
    }
}