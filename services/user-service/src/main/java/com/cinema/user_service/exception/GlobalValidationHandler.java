package com.cinema.user_service.exception;

import com.cinema.user_service.dto.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<Map<String , String>>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException  e){
        Map<String , String> map =  new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->{
            map.put(error.getField() , error.getDefaultMessage());
        } );
        ApiResponse<Map<String  , String>> response = new ApiResponse<>(false  ,   "Validation failed", map);

        return ResponseEntity
                .badRequest()
                .body(response);

    }
}
