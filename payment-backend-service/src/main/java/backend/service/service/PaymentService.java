package backend.service.service;

import backend.service.repository.PaymentRepository;
import backend.template.dto.PaymentDto;
import backend.template.dto.PaymentResponseDto;
import backend.template.entity.Payment;
import backend.template.provider.PaymentProvider;
import backend.template.transformer.PaymentTransformer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {

    public static final String NOT_FOUND_PAYMENT_ERROR_MESSAGE = "Payment with id='%s' does not exist";

    private final Map<String, PaymentProvider<PaymentDto, Payment>> providerMap;

    private final Map<String, PaymentTransformer<Payment, PaymentResponseDto>> transformerMap;

    private final PaymentRepository repository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PaymentService(Map<String, PaymentProvider<PaymentDto, Payment>> serviceLogicMap,
                          Map<String, PaymentTransformer<Payment, PaymentResponseDto>>  transformerMap,
                          PaymentRepository repository) {
        this.providerMap = serviceLogicMap;
        this.transformerMap = transformerMap;
        this.repository = repository;
    }

    public PaymentResponseDto cancelPayment(String type, String id) {
        PaymentProvider<PaymentDto, Payment> paymentProvider = providerMap.get(type);
        Payment payment = getPaymentById(id);
        Payment cancelledPayment = paymentProvider.cancelPayment(payment);
        repository.save(cancelledPayment);
        return transformerMap.get(type).transformToResponseDto(cancelledPayment);
    }

    public PaymentResponseDto startPayment(PaymentDto paymentDto) {
        String type = paymentDto.getType();
        PaymentProvider<PaymentDto, Payment> paymentProvider = providerMap.get(type);
        Payment payment = paymentProvider.getInitialPayment();
        repository.saveAndFlush(payment);
        paymentDto.setId(payment.getId());
        Payment processedPayment = paymentProvider.startPayment(paymentDto);
        repository.save(processedPayment);
        return transformerMap.get(type).transformToResponseDto(processedPayment);
    }

    public PaymentResponseDto refundPayment(String type, String id) {
        PaymentProvider<PaymentDto, Payment> paymentProvider = providerMap.get(type);
        Payment payment = getPaymentById(id);
        Payment refundedPayment = paymentProvider.refundPayment(payment);
        repository.save(refundedPayment);
        return transformerMap.get(type).transformToResponseDto(refundedPayment);
    }

    public PaymentResponseDto updatePayment(PaymentDto paymentDto) {
        String type = paymentDto.getType();
        getPaymentById(paymentDto.getId());
        PaymentProvider<PaymentDto, Payment> paymentProvider = providerMap.get(type);
        Payment updatedPayment = paymentProvider.updatePayment(paymentDto);
        repository.save(updatedPayment);
        return transformerMap.get(type).transformToPaymentResponseDto(updatedPayment);
    }

    public PaymentResponseDto capturePayment(String type, String id) {
        PaymentProvider<PaymentDto, Payment> paymentProvider = providerMap.get(type);
        Payment payment = getPaymentById(id);
        Payment capturedPayment = paymentProvider.capturePayment(payment);
        repository.save(capturedPayment);
        return transformerMap.get(type).transformToResponseDto(capturedPayment);
    }


    public PaymentResponseDto getPayment(String type, String id) {
        return transformerMap.get(type).transformToPaymentResponseDto(getPaymentById(id));
    }


    private Payment getPaymentById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new PaymentNotFoundException(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, id))
        );
    }

}
