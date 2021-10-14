package com.example.bluecodepay.bluecode.service;


import backend.template.handler.HystrixExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BluecodeExceptionHandler implements HystrixExceptionHandler<BluecodeException> {

    @Override
    public boolean isAppropriateHandler(Throwable exception) {
        return exception instanceof BluecodeException;
    }

    @Override
    public ResponseEntity<String> handleException(BluecodeException exception) {
        return  ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
    }
}
