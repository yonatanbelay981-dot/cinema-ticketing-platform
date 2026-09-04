package com.cinema.seat_service.exception;

public class SeatNotHallException extends RuntimeException{

    public SeatNotHallException(String messages){
        super(messages);
    }

}
