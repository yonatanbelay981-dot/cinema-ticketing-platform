package com.cinema.seat_service.exception;

import com.cinema.seat_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>>SeatNotFoundExceptionHandler(SeatNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        e.getMessage(),
                        null
                )
        );

    }

    @ExceptionHandler(SeatNotHallException.class)
    public ResponseEntity<ApiResponse<Object>>SeatNotHallFoundExceptionHandler(SeatNotHallException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        e.getMessage(),
                        null
                )
        );

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        "Validation failed",
                        errors
                )
        );
    }
}
