package com.cinema.analytics_service.controller;

import com.cinema.analytics_service.dto.ApiResponse;
import com.cinema.analytics_service.dto.SalesAnalyticResponse;
import com.cinema.analytics_service.service.SalesAnalyticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics/sales")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    public SalesAnalyticsController(
            SalesAnalyticsService salesAnalyticsService
    ) {
        this.salesAnalyticsService = salesAnalyticsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SalesAnalyticResponse>>> getAllSalesAnalytics(
            Pageable pageable
    ) {

        Page<SalesAnalyticResponse> analytics =
                salesAnalyticsService.getAllSalesAnalytics(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Sales analytics fetched successfully",
                        analytics
                )
        );
    }

    @GetMapping("/{date}")
    public ResponseEntity<ApiResponse<SalesAnalyticResponse>> getSalesAnalyticsByDate(
            @PathVariable LocalDate date
    ) {

        SalesAnalyticResponse analytics =
                salesAnalyticsService.getSalesAnalyticsByDate(date);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Sales analytics fetched successfully",
                        analytics
                )
        );
    }
}