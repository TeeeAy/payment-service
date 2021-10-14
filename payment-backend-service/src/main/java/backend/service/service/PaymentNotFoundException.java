package backend.service.service;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message){
        super(message);
    }
}
