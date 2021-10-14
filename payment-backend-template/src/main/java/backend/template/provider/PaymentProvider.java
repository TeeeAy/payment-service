package backend.template.provider;

public interface PaymentProvider<T, R> {

    default R startPayment(T paymentDto) {
        throw new UnsupportedOperationException();
    }

    default R cancelPayment(R payment) {
        throw new UnsupportedOperationException();
    }

    default R updatePayment(T paymentDto) {
        throw new UnsupportedOperationException();
    }

    default R capturePayment(R payment) {
        throw new UnsupportedOperationException();
    }

    default R refundPayment(R payment) {
        throw new UnsupportedOperationException();
    }

    default R getPayment(String id) {
        throw new UnsupportedOperationException();
    }


    R getInitialPayment();

    String getType();

}
