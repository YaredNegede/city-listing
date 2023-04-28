package com.wemakesoftware.citilistingservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CityListingControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Exception> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(new Exception(exception.getLocalizedMessage()));
    }
}
