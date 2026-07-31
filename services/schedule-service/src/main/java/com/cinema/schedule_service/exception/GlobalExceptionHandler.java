package com.cinema.schedule_service.exception;

import com.cinema.schedule_service.dto.ApiResponse;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ShowTimeNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleShowTimeNotFound(ShowTimeNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND) .body(
                new ApiResponse<>(
                        false,
                        e.getMessage(),
                        null
                )
        );}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String , String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        "Invalid input",
                        errors
                )
        );
    }
}
