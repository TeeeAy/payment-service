package com.example.bluecodepay.bluecode.integration.service;

import backend.template.entity.Status;
import com.example.bluecodepay.bluecode.config.BluecodeConfig;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import com.example.bluecodepay.bluecode.integration.config.WireMockServer;
import com.example.bluecodepay.bluecode.service.BluecodeException;
import com.example.bluecodepay.bluecode.service.BluecodeProvider;
import com.example.bluecodepay.bluecode.test.util.BluecodeClientMocker;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static com.example.bluecodepay.bluecode.test.constant.TestConstants.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(classes = BluecodeConfig.class)
@EnableAutoConfiguration
@ActiveProfiles("test")
@WireMockServer
public class BluecodeProviderTest {


    @Autowired
    private BluecodeProvider bluecodeProvider;


    @Test
    public void shouldCancelPayment() throws IOException {
        BluecodePayment bluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.CANCELLED)
                .build();

        BluecodePayment cancelledBluecodePayment = bluecodePayment.toBuilder()
                .withStatus(Status.CANCELLED)
                .build();

        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/initial_payment.json")
                .withResponseBody("responses/cancel_response.json")
                .withStatus(200)
                .build();

        BluecodeClientMocker.mockCancelPayment(mockInf);

        assertEquals(cancelledBluecodePayment, bluecodeProvider.cancelPayment(bluecodePayment));

    }


    @Test
    public void shouldThrowHystrixRuntimeExceptionOnCancelPayment() throws IOException {
        BluecodePayment bluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.PENDING)
                .build();



        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/initial_payment.json")
                .withResponseBody("responses/cancel_404_error_response.json")
                .withStatus(404)
                .build();

        BluecodeClientMocker.mockCancelPayment(mockInf);


        HystrixRuntimeException hystrixRuntimeException = assertThrows(HystrixRuntimeException.class,
                () -> bluecodeProvider.cancelPayment(bluecodePayment));
        assertEquals(hystrixRuntimeException.getFallbackException().getCause().getCause().getClass(),
                BluecodeException.class);
    }

    @Test
    void shouldStartApprovedPayment() throws IOException {
        BluecodePayment startedBluecodePayment = BluecodePayment.builder()
                .withStatus(Status.SUCCESS)
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withAcquirerTxId(ACQUIRER_ID)
                .build();

        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/initial_payment.json")
                .withResponseBody("responses/start_payment_approved_response.json")
                .withStatus(200)
                .build();

        BluecodeClientMocker.mockStartPayment(mockInf);

        BluecodePaymentDto bluecodePaymentDto = BluecodePaymentDto.builder()
                .withAmount(AMOUNT)
                .withId(PAYMENT_ID)
                .build();

        assertEquals(startedBluecodePayment, bluecodeProvider.startPayment(bluecodePaymentDto));
    }


    @Test
    void shouldSaveDeclinedPayment() throws IOException {

        BluecodePayment declinedBluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.ERROR)
                .withAcquirerTxId(ACQUIRER_ID)
                .build();

        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/initial_payment.json")
                .withResponseBody("responses/start_payment_declined_response.json")
                .withStatus(402)
                .build();

        BluecodeClientMocker.mockStartPayment(mockInf);


        BluecodePaymentDto bluecodePaymentDto = BluecodePaymentDto.builder()
                .withAmount(AMOUNT)
                .withId(PAYMENT_ID)
                .build();

        assertEquals(declinedBluecodePayment, bluecodeProvider.startPayment(bluecodePaymentDto));
    }


    @Test
    void shouldStartPaymentAfterProcessing() throws IOException {
        BluecodePaymentDto bluecodePaymentDto = BluecodePaymentDto.builder()
                .withAmount(AMOUNT)
                .withId(PAYMENT_ID)
                .build();

        BluecodePayment startedBluecodePayment = BluecodePayment.builder()
                .withStatus(Status.SUCCESS)
                .withId(PAYMENT_ID)
                .withAcquirerTxId(ACQUIRER_ID)
                .withRequestedAmount(AMOUNT)
                .build();

        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/initial_payment.json")
                .withResponseBody("responses/start_payment_processing_response.json")
                .withStatus(200)
                .build();

        BluecodeClientMocker.mockStartPayment(mockInf);

        BluecodeClientMocker.MockInf processingMockInf = mockInf.toBuilder()
                .withCurrentScenarioState(STARTED)
                .withNextScenarioState("Payment Approved")
                .withScenario("Retry Scenario")
                .build();

        BluecodeClientMocker.MockInf approvedMockInf = processingMockInf.toBuilder()
                .withCurrentScenarioState("Payment Approved")
                .withResponseBody("responses/start_payment_approved_response.json")
                .withNextScenarioState("Payment Success")
                .build();

        BluecodeClientMocker.mockStatusCheck(processingMockInf);

        BluecodeClientMocker.mockStatusCheck(approvedMockInf );

        assertEquals(startedBluecodePayment, bluecodeProvider.startPayment(bluecodePaymentDto));

    }


    @Test
    void shouldRefundPayment() throws IOException {

        BluecodePayment bluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.SUCCESS)
                .withAcquirerTxId(ACQUIRER_ID)
                .build();

        BluecodePayment refundedBluecodePayment = bluecodePayment.toBuilder()
                .withStatus(Status.REFUNDED)
                .build();

        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/bluecode_refund.json")
                .withResponseBody("responses/refund_response.json")
                .withStatus(200)
                .build();


        BluecodeClientMocker.mockRefundPayment(mockInf);

        assertEquals(refundedBluecodePayment, bluecodeProvider.refundPayment(bluecodePayment));

    }

    @Test
    public void shouldThrowHystrixRuntimeExceptionOnRefundPayment() throws IOException {
        BluecodePayment bluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.PENDING)
                .withAcquirerTxId(ACQUIRER_ID)
                .build();


        BluecodeClientMocker.MockInf mockInf = BluecodeClientMocker.MockInf.builder()
                .withRequestBody("requests/bluecode_refund.json")
                .withResponseBody("responses/refund_502_error_response.json")
                .withStatus(502)
                .build();

        BluecodeClientMocker.mockRefundPayment(mockInf);

        HystrixRuntimeException hystrixRuntimeException = assertThrows(HystrixRuntimeException.class,
                () -> bluecodeProvider.refundPayment(bluecodePayment));
        assertEquals(hystrixRuntimeException.getFallbackException().getCause().getCause().getClass(),
                BluecodeException.class);

    }


}