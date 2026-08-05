package com.cinema.Booking_service.exception;

import com.cinema.Booking_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> bookingNotFoundExceptionHandler(BookingNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        e.getMessage(),
                        null
                )
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object >>MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        Map<String , String> map =  new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error->{
            map.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                        false,
                        "Invalid request data",
                        map
                )
        );
    }
}
