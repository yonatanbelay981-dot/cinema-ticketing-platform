package com.cinema.analytics_service.repository;


import com.cinema.analytics_service.entity.SalesAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface SalesAnalyticsRepository
        extends JpaRepository<SalesAnalytics, UUID> {

    Optional<SalesAnalytics> findByDate(LocalDate date);
}
