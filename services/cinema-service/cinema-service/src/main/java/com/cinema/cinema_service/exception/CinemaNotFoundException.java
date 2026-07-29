package com.cinema.cinema_service.exception;

public class CinemaNotFoundException extends RuntimeException{
    public  CinemaNotFoundException(String message){
        super(message);

    }
}
