package com.cinema.analytics_service.exception;

import com.cinema.analytics_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AnalyticMovieNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> bookingNotFoundExceptionHandler(AnalyticMovieNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        e.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(SalesAnalyticsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> salesAnalyticsNotFoundException(SalesAnalyticsNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        e.getMessage(),
                        null
                )
        );
    }
}
