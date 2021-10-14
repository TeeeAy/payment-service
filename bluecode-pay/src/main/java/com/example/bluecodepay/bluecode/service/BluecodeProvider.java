package com.example.bluecodepay.bluecode.service;

import backend.template.entity.Status;
import backend.template.provider.PaymentProvider;
import com.example.bluecodepay.bluecode.client.BluecodeClient;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import com.example.bluecodepay.bluecode.entity.BluecodeRefund;
import com.example.bluecodepay.bluecode.response.BluecodePaymentInfo;
import com.example.bluecodepay.bluecode.response.BluecodeResponse;
import com.example.bluecodepay.bluecode.response.Result;
import com.example.bluecodepay.bluecode.transformer.BluecodePaymentTransformer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
public class BluecodeProvider implements PaymentProvider<BluecodePaymentDto, BluecodePayment> {

    private final BluecodePaymentTransformer bluecodePaymentTransformer;

    private final BluecodeClient bluecodeClient;

    private final RetryManager retryManager;

    public BluecodeProvider(
            BluecodePaymentTransformer bluecodePaymentTransformer,
            BluecodeClient bluecodeClient,
            RetryManager retryManager) {
        this.bluecodePaymentTransformer = bluecodePaymentTransformer;
        this.bluecodeClient = bluecodeClient;
        this.retryManager = retryManager;
    }


    @Override
    public BluecodePayment getInitialPayment() {
        return new BluecodePayment();
    }


    @Override
    public BluecodePayment startPayment(BluecodePaymentDto paymentDto) {
        BluecodePayment payment = bluecodePaymentTransformer.fromDto(paymentDto);
        ResponseEntity<BluecodeResponse> responseEntity = bluecodeClient.startPayment(payment);
        BluecodeResponse response = Objects.requireNonNull(responseEntity.getBody());
        if (response.getResult() == Result.PROCESSING) {
            response = processPendingResponse(payment, response);
        }
        BluecodePaymentInfo paymentInfo = response.getPayment();
        processResponse(payment, paymentInfo);
        return payment;
    }


    private BluecodeResponse processPendingResponse(BluecodePayment payment, BluecodeResponse response) {
        int ttl = response.getStatus().getTtl();
        int checkStatusIn = response.getStatus().getCheckStatusIn();
        Predicate<BluecodeResponse> condition = (resp) -> resp.getResult() == Result.PROCESSING;
        Supplier<BluecodeResponse> action = () -> bluecodeClient.checkStatus(payment);
        RetryManager.RetryInf retryInf = RetryManager.RetryInf.builder()
                .withAction(action)
                .withCondition(condition)
                .withMaxAttempts(ttl/checkStatusIn)
                .withWaitDuration(checkStatusIn)
                .build();
        return retryManager.retryRequest(retryInf);
    }

    private void processResponse(BluecodePayment payment, BluecodePaymentInfo paymentInfo) {
        payment.setStatus(paymentInfo.getState().getCorrespondingStatus());
        payment.setAcquirerTxId(paymentInfo.getAcquirerTxId());
    }

    @Override
    public BluecodePayment cancelPayment(BluecodePayment payment) {
        bluecodeClient.cancelPayment(payment);
        payment.setStatus(Status.CANCELLED);
        return payment;
    }

    @Override
    public BluecodePayment refundPayment(BluecodePayment payment) {
        BluecodeRefund refund = BluecodeRefund
                .builder()
                .withAcquirerTxId(payment.getAcquirerTxId())
                .withAmount(payment.getRequestedAmount())
                .build();
        bluecodeClient.refundPayment(refund);
        payment.setStatus(Status.REFUNDED);
        return payment;
    }

    @Override
    public String getType() {
        return "bluecode";
    }
}