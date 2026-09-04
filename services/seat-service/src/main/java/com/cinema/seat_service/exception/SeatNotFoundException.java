package com.cinema.seat_service.exception;

public class SeatNotFoundException extends RuntimeException{
    public SeatNotFoundException(String message){
        super(message);
    }
}
