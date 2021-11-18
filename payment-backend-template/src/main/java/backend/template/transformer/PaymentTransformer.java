package backend.template.transformer;

public interface PaymentTransformer<T,R> {

    R transformToResponseDto(T payment);

    R transformToPaymentResponseDto(T payment);

    String getType();

}
