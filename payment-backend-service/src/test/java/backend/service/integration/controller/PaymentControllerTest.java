package backend.service.integration.controller;

import backend.service.PaymentServiceApp;
import backend.service.integration.configutation.TestConfig;
import backend.service.repository.PaymentRepository;
import backend.service.test.bean.TestPayment;
import backend.service.test.bean.TestPaymentDto;
import backend.service.test.bean.TestPaymentResponseDto;
import backend.template.entity.Status;
import backend.template.provider.PaymentProvider;
import backend.template.transformer.PaymentTransformer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.Optional;

import static backend.service.service.PaymentService.NOT_FOUND_PAYMENT_ERROR_MESSAGE;
import static backend.service.test.constant.TestConstants.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {PaymentServiceApp.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class
})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PaymentRepository repository;

    @Autowired
    private PaymentProvider<TestPaymentDto, TestPayment> paymentProvider;

    @Autowired
    private PaymentTransformer<TestPayment, TestPaymentResponseDto> paymentTransformer;

    @Test
    void shouldStartPayment() {

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

        webTestClient
                .post()
                .uri("/start-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testPaymentDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TestPaymentResponseDto.class).isEqualTo(testPaymentResponseDto);


        then(repository).should(times(1)).saveAndFlush(initialTestPayment);
        then(repository).should(times(1)).save(processedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getType();
        then(paymentProvider).should(times(1)).getInitialPayment();
        then(paymentProvider).should(times(1)).startPayment(testPaymentDto);
        then(paymentProvider).shouldHaveNoMoreInteractions();
        then(paymentTransformer).should(times(1)).getType();
        then(paymentTransformer).should(times(1)).transformToResponseDto(processedTestPayment);
        then(paymentTransformer).shouldHaveNoMoreInteractions();

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

        webTestClient
                .get()
                .uri("/get-payment/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TestPaymentResponseDto.class).isEqualTo(testPaymentResponseDto);


        then(repository).should(only()).findById(PAYMENT_ID);
        then(paymentProvider).should(only()).getType();
        then(paymentTransformer).should(times(1)).getType();
        then(paymentTransformer).should(times(1)).transformToPaymentResponseDto(testPayment);
        then(paymentTransformer).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldRaisePaymentNotFoundExceptionOnCancelPayment() {
        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        webTestClient
                .delete()
                .uri("/cancel/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID));

        then(repository).should(only()).findById(PAYMENT_ID);
    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnCapturePayment() {
        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        webTestClient
                .delete()
                .uri("/capture-payment/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID));

        then(repository).should(only()).findById(PAYMENT_ID);
    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnRefundPayment() {
        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        webTestClient
                .delete()
                .uri("/refund/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID));

        then(repository).should(only()).findById(PAYMENT_ID);
    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnUpdatePayment() {
        final TestPaymentDto testPaymentDto = TestPaymentDto.builder()
                .withId(PAYMENT_ID)
                .withAmount(AMOUNT)
                .build();

        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        webTestClient
                .put()
                .uri("/update-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testPaymentDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID));


        then(repository).should(only()).findById(PAYMENT_ID);
    }


    @Test
    void shouldRaisePaymentNotFoundExceptionOnGetPayment() {
        given(repository.findById(PAYMENT_ID)).willReturn(Optional.empty());

        webTestClient
                .get()
                .uri("/get-payment/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo(String.format(NOT_FOUND_PAYMENT_ERROR_MESSAGE, PAYMENT_ID));

        then(repository).should(only()).findById(PAYMENT_ID);
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

        webTestClient
                .delete()
                .uri("/cancel/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TestPaymentResponseDto.class).isEqualTo(testPaymentResponseDto);

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(cancelledTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getType();
        then(paymentProvider).should(times(1)).cancelPayment(testPayment);
        then(paymentProvider).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getType();
        then(paymentTransformer).should(times(1)).transformToResponseDto(cancelledTestPayment);
        then(paymentProvider).shouldHaveNoMoreInteractions();

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

        webTestClient
                .delete()
                .uri("/refund/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TestPaymentResponseDto.class).isEqualTo(testPaymentResponseDto);

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(refundedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getType();
        then(paymentProvider).should(times(1)).refundPayment(testPayment);
        then(paymentProvider).shouldHaveNoMoreInteractions();
        then(paymentTransformer).should(times(1)).getType();
        then(paymentTransformer).should(times(1)).transformToResponseDto(refundedTestPayment);
        then(paymentTransformer).shouldHaveNoMoreInteractions();

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

        webTestClient
                .put()
                .uri("/update-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testPaymentDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TestPaymentResponseDto.class).isEqualTo(testPaymentResponseDto);

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(updatedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getType();
        then(paymentProvider).should(times(1)).updatePayment(testPaymentDto);
        then(paymentProvider).shouldHaveNoMoreInteractions();
        then(paymentTransformer).should(times(1)).getType();
        then(paymentTransformer).should(times(1)).transformToPaymentResponseDto(updatedTestPayment);
        then(paymentTransformer).shouldHaveNoMoreInteractions();
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

        webTestClient
                .delete()
                .uri("/capture-payment/" + PAYMENT_TYPE + "/" + PAYMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TestPaymentResponseDto.class).isEqualTo(testPaymentResponseDto);

        then(repository).should(times(1)).findById(PAYMENT_ID);
        then(repository).should(times(1)).save(capturedTestPayment);
        then(repository).shouldHaveNoMoreInteractions();
        then(paymentProvider).should(times(1)).getType();
        then(paymentProvider).should(times(1)).capturePayment(testPayment);
        then(paymentProvider).shouldHaveNoMoreInteractions();
        then(paymentTransformer).should(times(1)).getType();
        then(paymentTransformer).should(times(1)).transformToResponseDto(capturedTestPayment);
        then(paymentTransformer).shouldHaveNoMoreInteractions();
    }
}