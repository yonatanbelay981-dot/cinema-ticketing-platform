package com.cinema.movie_services.exception;

public class GenreNotFoundException extends RuntimeException{
    public GenreNotFoundException(String message){
        super(message);
    }
}
