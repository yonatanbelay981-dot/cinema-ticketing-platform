package com.cinema.analytics_service.repository;


import com.cinema.analytics_service.entity.BookingAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingAnalyticsRepository
        extends JpaRepository<BookingAnalytics, UUID> {

    Optional<BookingAnalytics> findByDate(LocalDate date);
}
