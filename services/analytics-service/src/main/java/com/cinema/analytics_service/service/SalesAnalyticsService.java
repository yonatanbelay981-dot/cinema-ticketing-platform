package com.cinema.analytics_service.service;


import com.cinema.analytics_service.dto.SalesAnalyticResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SalesAnalyticsService {

    SalesAnalyticResponse getSalesAnalyticsByDate(LocalDate date);

    Page<SalesAnalyticResponse> getAllSalesAnalytics(Pageable pageable);
}
