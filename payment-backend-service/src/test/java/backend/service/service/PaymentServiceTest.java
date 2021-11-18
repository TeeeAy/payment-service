package backend.service.service;

import backend.service.repository.PaymentRepository;
import backend.service.test.bean.TestPayment;
import backend.service.test.bean.TestPaymentDto;
import backend.service.test.bean.TestPaymentResponseDto;
import backend.template.dto.PaymentDto;
import backend.template.dto.PaymentResponseDto;
import backend.template.entity.Payment;
import backend.template.entity.Status;
import backend.template.provider.PaymentProvider;
import backend.template.transformer.PaymentTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static backend.service.service.PaymentService.NOT_FOUND_PAYMENT_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static backend.service.test.constant.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentTransformer<Payment, PaymentResponseDto> paymentTransformer;

    @Mock
    private PaymentProvider<PaymentDto, Payment> paymentProvider;

    @Mock
    private PaymentRepository repository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        final Map<String, PaymentProvider<PaymentDto, Payment>> providerMap =
                Collections.singletonMap(PAYMENT_TYPE, paymentProvider);
        final Map<String, PaymentTransformer<Payment, PaymentResponseDto>> transformerMap =
                Collections.singletonMap(PAYMENT_TYPE, paymentTransformer);
        paymentService = new PaymentService(providerMap, transformerMap, repository);
    }

    @Test
    void shouldCancelPayment() {
        final TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.PENDING)
                .build();

        final TestPayment cancelledTestPayment = testPayment.toBuilder()
                .withStatus(Status.CANCELLED)
                .build();

        final TestPaymentResponseDto testPaymentResponseDto = TestPaymentResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.CANCELLED)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.of(testPayment));
        given(paymentProvider.cancelPayment(testPayment)).willReturn(cancelledTestPayment);
        given(paymentTransformer.transformToResponseDto(cancelledTestPayment)).willReturn(testPaymentResponseDto);

        assertEquals(testPaymentResponseDto, paymentService.cancelPayment(PAYMENT_TYPE, PAYMENT_ID));

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(cancelledTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(only()).cancelPayment(testPayment);
        then(paymentTransformer).should(only()).transformToResponseDto(cancelledTestPayment);
    }

    @Test
    void shouldRaisePaymentNotFoundExceptionOnCancelPayment() {

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        final PaymentNotFoundException exception =
                assertThrows(PaymentNotFoundException.class, () -> paymentService.cancelPayment(PAYMENT_TYPE
                        , PAYMENT_ID));

        assertEquals(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID), exception.getMessage());

        then(repository).should(only()).findById(PAYMENT_ID);
        then(paymentProvider).shouldHaveNoInteractions();
        then(paymentTransformer).shouldHaveNoInteractions();
    }


    @Test
    void startPayment() {
        final TestPaymentDto testPaymentDto = TestPaymentDto.builder()
                .withId(PAYMENT_ID)
                .withAmount(AMOUNT)
                .build();

        final TestPayment initialTestPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.PENDING)
                .build();

        final TestPayment processedTestPayment = initialTestPayment.toBuilder()
                .withStatus(Status.SUCCESS)
                .build();

        final TestPaymentResponseDto testPaymentResponseDto = TestPaymentResponseDto.builder()
                .withStatus(Status.SUCCESS)
                .withId(PAYMENT_ID)
                .withAmount(AMOUNT)
                .build();

        given(paymentProvider.getInitialPayment()).willReturn(initialTestPayment);
        given(paymentProvider.startPayment(testPaymentDto)).willReturn(processedTestPayment);
        given(paymentTransformer.transformToResponseDto(processedTestPayment)).willReturn(testPaymentResponseDto);

        assertEquals(testPaymentResponseDto, paymentService.startPayment(testPaymentDto));

        then(repository).should(times(1)).saveAndFlush(initialTestPayment);
        then(repository).should(times(1)).save(processedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getInitialPayment();
        then(paymentProvider).should(times(1)).startPayment(testPaymentDto);
        then(paymentProvider).shouldHaveNoMoreInteractions();
        then(paymentTransformer).should(only()).transformToResponseDto(processedTestPayment);
    }

    @Test
    void shouldRefundPayment() {
        final TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.SUCCESS)
                .build();

        final TestPayment refundedTestPayment = testPayment.toBuilder()
                .withStatus(Status.REFUNDED)
                .build();

        final TestPaymentResponseDto testPaymentResponseDto = TestPaymentResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.REFUNDED)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.of(testPayment));
        given(paymentProvider.refundPayment(testPayment)).willReturn(refundedTestPayment);
        given(paymentTransformer.transformToResponseDto(refundedTestPayment)).willReturn(testPaymentResponseDto);

        assertEquals(testPaymentResponseDto, paymentService.refundPayment(PAYMENT_TYPE, PAYMENT_ID));

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(refundedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(only()).refundPayment(testPayment);
        then(paymentTransformer).should(only()).transformToResponseDto(refundedTestPayment);
    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnRefundPayment() {

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        final PaymentNotFoundException exception =
                assertThrows(PaymentNotFoundException.class, () -> paymentService.refundPayment(PAYMENT_TYPE
                        , PAYMENT_ID));

        assertEquals(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID), exception.getMessage());

        then(repository).should(only()).findById(PAYMENT_ID);
        then(paymentProvider).shouldHaveNoInteractions();
        then(paymentTransformer).shouldHaveNoInteractions();
    }

    @Test
    void shouldUpdatePayment() {

        final TestPaymentDto testPaymentDto = TestPaymentDto.builder()
                .withId(PAYMENT_ID)
                .withAmount(AMOUNT)
                .build();

        final TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.SUCCESS)
                .build();

        final TestPayment updatedTestPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.SUCCESS)
                .build();

        final TestPaymentResponseDto testPaymentResponseDto = TestPaymentResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.SUCCESS)
                .withAmount(AMOUNT)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.of(testPayment));
        given(paymentProvider.updatePayment(testPaymentDto)).willReturn(updatedTestPayment);
        given(paymentTransformer.transformToPaymentResponseDto(updatedTestPayment)).willReturn(testPaymentResponseDto);

        assertEquals(testPaymentResponseDto, paymentService.updatePayment(testPaymentDto));

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(updatedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(only()).updatePayment(testPaymentDto);
        then(paymentTransformer).should(only()).transformToPaymentResponseDto(updatedTestPayment);

    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnUpdatePayment() {

        final TestPaymentDto testPaymentDto = TestPaymentDto.builder()
                .withId(PAYMENT_ID)
                .withAmount(AMOUNT)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        final PaymentNotFoundException exception =
                assertThrows(PaymentNotFoundException.class, () -> paymentService.updatePayment(testPaymentDto));

        assertEquals(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID), exception.getMessage());

        then(repository).should(only()).findById(PAYMENT_ID);
        then(paymentProvider).shouldHaveNoInteractions();
        then(paymentTransformer).shouldHaveNoInteractions();
    }

    @Test
    void shouldCapturePayment() {
        final TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.PENDING)
                .build();

        final TestPayment capturedTestPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.CAPTURED)
                .build();

        final TestPaymentResponseDto testPaymentResponseDto = TestPaymentResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.CAPTURED)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.of(testPayment));
        given(paymentProvider.capturePayment(testPayment)).willReturn(capturedTestPayment);
        given(paymentTransformer.transformToResponseDto(capturedTestPayment)).willReturn(testPaymentResponseDto);

        assertEquals(testPaymentResponseDto, paymentService.capturePayment(PAYMENT_TYPE, PAYMENT_ID));

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(capturedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(only()).capturePayment(testPayment);
        then(paymentTransformer).should(only()).transformToResponseDto(capturedTestPayment);
    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnCapturePayment() {

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        final PaymentNotFoundException exception =
                assertThrows(PaymentNotFoundException.class, () -> paymentService.capturePayment(PAYMENT_TYPE
                        , PAYMENT_ID));

        assertEquals(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID), exception.getMessage());

        then(repository).should(only()).findById(PAYMENT_ID);

        then(paymentProvider).shouldHaveNoInteractions();
        then(paymentTransformer).shouldHaveNoInteractions();
    }

    @Test
    void shouldGetPayment() {
        final TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.CANCELLED)
                .build();

        final TestPaymentResponseDto testPaymentResponseDto = TestPaymentResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.CANCELLED)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.of(testPayment));
        given(paymentTransformer.transformToPaymentResponseDto(testPayment)).willReturn(testPaymentResponseDto);

        assertEquals(testPaymentResponseDto, paymentService.getPayment(PAYMENT_TYPE, PAYMENT_ID));

        then(repository).should(only()).findById(PAYMENT_ID);
        then(paymentTransformer).should(only()).transformToPaymentResponseDto(testPayment);
        then(paymentProvider).shouldHaveNoInteractions();
    }

    @Test
    void shouldRaisePaymentNotFoundExceptionOnGetPayment() {
        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        final PaymentNotFoundException exception =
                assertThrows(PaymentNotFoundException.class, () -> paymentService.getPayment(PAYMENT_TYPE
                        , PAYMENT_ID));

        assertEquals(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID), exception.getMessage());

        then(repository).should(only()).findById(PAYMENT_ID);
        then(paymentProvider).shouldHaveNoInteractions();
        then(paymentTransformer).shouldHaveNoInteractions();
    }
}