package com.cinema.analytics_service.repository;


import com.cinema.analytics_service.entity.ProcessedAnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedAnalyticsEventRepository
        extends JpaRepository<ProcessedAnalyticsEvent, UUID> {

    boolean existsByEventIdAndProcessorType(
            UUID eventId,
            String processorType
    );
}
