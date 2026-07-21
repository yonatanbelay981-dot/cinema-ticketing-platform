package com.cinema.user_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalValidationHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException  e){
        Map<String , String> map =  new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error ->{
            String fieldError = ((FieldError)error).getField();
            String value = error.getDefaultMessage();
            map.put(fieldError , value);
        } );

        return ResponseEntity
                .badRequest()
                .body(map);

    }
}
