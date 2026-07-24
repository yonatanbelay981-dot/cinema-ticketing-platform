package com.cinema.movie_services.exception;

import com.cinema.movie_services.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                )
        );

    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> duplicateResourceExceptionHandler(DuplicateResourceException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null

                )
        );
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> genreNotFoundExceptionHandler(GenreNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Object>> invalidRequestExceptionHandler(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                )
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            map.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        false,
                        "Validation failed",
                        map
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> globalExceptionHandler(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(
                        false,
                        "An unexpected error occurred",
                        null
                )
        );

    }
}
