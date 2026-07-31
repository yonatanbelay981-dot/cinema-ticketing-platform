package com.cinema.schedule_service.exception;

public class ShowTimeNotFoundException extends RuntimeException{
    public ShowTimeNotFoundException(String message) {
        super(message);
    }
}
