package com.cinema.seat_service.exception;

public class SeatUnavailableException  extends RuntimeException{
    public SeatUnavailableException(String message){
        super(message);
    }
}
