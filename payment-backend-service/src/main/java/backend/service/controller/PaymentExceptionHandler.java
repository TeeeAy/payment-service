package backend.service.controller;

import backend.service.service.PaymentNotFoundException;
import backend.template.handler.HystrixExceptionHandler;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class PaymentExceptionHandler{

    private final List<HystrixExceptionHandler<Throwable>> hystrixExceptionHandlers;

    @Autowired
    public PaymentExceptionHandler(List<HystrixExceptionHandler<Throwable>> hystrixExceptionHandlers) {
        this.hystrixExceptionHandlers = hystrixExceptionHandlers;
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    public ResponseEntity<String> handleException(HystrixRuntimeException hystrixRuntimeException) {
        Throwable cause = hystrixRuntimeException.getFallbackException().getCause().getCause();
        HystrixExceptionHandler<Throwable> exceptionHandler = hystrixExceptionHandlers.stream()
                .filter((heh) -> heh.isAppropriateHandler(cause))
                .findFirst()
                .orElseThrow(() -> hystrixRuntimeException);
        return exceptionHandler.handleException(cause);
    }


    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handleException(PaymentNotFoundException paymentNotFoundException) {
        return ResponseEntity.badRequest().body(paymentNotFoundException.getMessage());
    }


}
