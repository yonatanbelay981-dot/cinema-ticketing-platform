package com.cinema.analytics_service.exception;

public class AnalyticMovieNotFoundException extends RuntimeException{
    public AnalyticMovieNotFoundException(String message){
        super(message);
    }
}
