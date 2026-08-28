package com.cinema.analytics_service.exception;

import org.springframework.data.jpa.repository.JpaRepository;

public class SalesAnalyticsNotFoundException extends RuntimeException {
    public SalesAnalyticsNotFoundException(String message){
        super(message);

    }
}
