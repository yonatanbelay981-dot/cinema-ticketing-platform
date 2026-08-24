package com.cinema.concession_service.exception;

public class FoodOrderNotFoundException
        extends RuntimeException {

    public FoodOrderNotFoundException(String message) {
        super(message);
    }
}
