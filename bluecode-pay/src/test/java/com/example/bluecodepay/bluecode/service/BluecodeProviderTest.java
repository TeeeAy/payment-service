package com.example.bluecodepay.bluecode.service;

import backend.template.entity.Status;
import com.example.bluecodepay.bluecode.client.BluecodeClient;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import com.example.bluecodepay.bluecode.entity.BluecodeRefund;
import com.example.bluecodepay.bluecode.response.*;
import com.example.bluecodepay.bluecode.transformer.BluecodePaymentTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.example.bluecodepay.bluecode.test.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.only;


@ExtendWith(MockitoExtension.class)
class BluecodeProviderTest {

    @Mock
    private BluecodePaymentTransformer bluecodePaymentTransformer;

    @Mock
    private BluecodeClient bluecodeClient;

    @Mock
    private RetryManager retryManager;

    @InjectMocks
    private BluecodeProvider bluecodeProvider;

    @Test
    void shouldStartPayment() {
        BluecodePayment initialBluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.PENDING)
                .build();

        BluecodePayment startedBluecodePayment = initialBluecodePayment.toBuilder()
                .withStatus(Status.SUCCESS)
                .build();

        BluecodePaymentInfo bluecodePaymentInfo = BluecodePaymentInfo.builder()
                .withState(State.APPROVED)
                .build();

        BluecodeResponse bluecodeResponse = BluecodeResponse.builder()
                .withResult(Result.OK)
                .withPayment(bluecodePaymentInfo)
                .build();

        ResponseEntity<BluecodeResponse> responseEntity = ResponseEntity.ok().body(bluecodeResponse);

        BluecodePaymentDto bluecodePaymentDto = BluecodePaymentDto.builder()
                .withAmount(AMOUNT)
                .withId(PAYMENT_ID)
                .build();

        given(bluecodePaymentTransformer.fromDto(bluecodePaymentDto)).willReturn(initialBluecodePayment);
        given(bluecodeClient.startPayment(initialBluecodePayment)).willReturn(responseEntity);

        assertEquals(startedBluecodePayment, bluecodeProvider.startPayment(bluecodePaymentDto));

        then(bluecodePaymentTransformer).should(only()).fromDto(bluecodePaymentDto);
        then(bluecodeClient).should(only()).startPayment(initialBluecodePayment);
        then(retryManager).shouldHaveNoInteractions();

    }


    @Test
    void shouldStartPaymentAfterProcessing() {
        BluecodePaymentDto bluecodePaymentDto = BluecodePaymentDto.builder()
                .withAmount(AMOUNT)
                .withId(PAYMENT_ID)
                .build();

        BluecodePayment initialBluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.PENDING)
                .build();

        BluecodePayment startedBluecodePayment = initialBluecodePayment.toBuilder()
                .withStatus(Status.SUCCESS)
                .build();

        BluecodePaymentStatus bluecodePaymentStatus = BluecodePaymentStatus.builder()
                .withCheckStatusIn(CHECK_STATUS_IN)
                .withTtl(TTL)
                .withMerchantTxId(PAYMENT_ID)
                .build();

        BluecodeResponse processingBluecodeResponse = BluecodeResponse.builder()
                .withStatus(bluecodePaymentStatus)
                .withResult(Result.PROCESSING)
                .build();

        ResponseEntity<BluecodeResponse> responseEntity = ResponseEntity.ok().body(processingBluecodeResponse);

        BluecodePaymentInfo bluecodePaymentInfo = BluecodePaymentInfo.builder()
                .withState(State.APPROVED)
                .build();

        BluecodeResponse okBluecodeResponse = BluecodeResponse.builder()
                .withPayment(bluecodePaymentInfo)
                .withResult(Result.OK)
                .build();

        RetryManager.RetryInf retryInf = RetryManager.RetryInf.builder()
                .withAction(() -> bluecodeClient.checkStatus(initialBluecodePayment))
                .withCondition((resp) -> resp.getResult() == Result.PROCESSING)
                .withMaxAttempts(bluecodePaymentStatus.getTtl() / bluecodePaymentStatus.getCheckStatusIn())
                .withWaitDuration(bluecodePaymentStatus.getCheckStatusIn())
                .build();

        given(bluecodePaymentTransformer.fromDto(bluecodePaymentDto)).willReturn(initialBluecodePayment);
        given(bluecodeClient.startPayment(initialBluecodePayment)).willReturn(responseEntity);
        given(retryManager.retryRequest(retryInf)).willCallRealMethod();
        given(bluecodeClient.checkStatus(initialBluecodePayment)).willReturn(processingBluecodeResponse,
                processingBluecodeResponse, okBluecodeResponse);

        assertEquals(startedBluecodePayment, bluecodeProvider.startPayment(bluecodePaymentDto));

        then(bluecodePaymentTransformer).should(only()).fromDto(bluecodePaymentDto);
        then(bluecodeClient).should(times(1)).startPayment(initialBluecodePayment);
        then(bluecodeClient).should(times(3)).checkStatus(initialBluecodePayment);
        then(bluecodeClient).shouldHaveNoMoreInteractions();
        then(retryManager).should(only()).retryRequest(retryInf);
    }


    @Test
    void shouldCancelPayment() {
        BluecodePayment bluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.PENDING)
                .build();

        BluecodePayment cancelledBluecodePayment = bluecodePayment.toBuilder()
                .withStatus(Status.CANCELLED)
                .build();

        BluecodeResponse bluecodeResponse = BluecodeResponse.builder()
                .withResult(Result.OK)
                .build();

        ResponseEntity<BluecodeResponse> responseEntity = ResponseEntity.ok().body(bluecodeResponse);

        given(bluecodeClient.cancelPayment(bluecodePayment)).willReturn(responseEntity);

        assertEquals(cancelledBluecodePayment, bluecodeProvider.cancelPayment(bluecodePayment));

        then(bluecodeClient).should(only()).cancelPayment(bluecodePayment);
        then(bluecodePaymentTransformer).shouldHaveNoInteractions();
        then(retryManager).shouldHaveNoInteractions();
    }


    @Test
    void shouldRefundPayment() {

        BluecodePayment bluecodePayment = BluecodePayment.builder()
                .withId(PAYMENT_ID)
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.SUCCESS)
                .build();

        BluecodeRefund bluecodeRefund = BluecodeRefund.builder()
                .withAcquirerTxId(bluecodePayment.getAcquirerTxId())
                .withAmount(AMOUNT)
                .build();

        BluecodePayment refundedBluecodePayment = bluecodePayment.toBuilder()
                .withStatus(Status.REFUNDED)
                .build();

        BluecodeResponse bluecodeResponse = BluecodeResponse.builder()
                .withResult(Result.OK)
                .build();

        ResponseEntity<BluecodeResponse> responseEntity = ResponseEntity.ok().body(bluecodeResponse);

        given(bluecodeClient.refundPayment(bluecodeRefund)).willReturn(responseEntity);

        assertEquals(refundedBluecodePayment, bluecodeProvider.refundPayment(bluecodePayment));

        then(bluecodeClient).should(only()).refundPayment(bluecodeRefund);
        then(bluecodePaymentTransformer).shouldHaveNoInteractions();
        then(retryManager).shouldHaveNoInteractions();
    }
}