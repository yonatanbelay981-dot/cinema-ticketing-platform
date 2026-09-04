package com.cinema.seat_service.exception;

public class SeatNotShowTimeException extends RuntimeException{
    public SeatNotShowTimeException(String message){
        super(message);
    }
}
