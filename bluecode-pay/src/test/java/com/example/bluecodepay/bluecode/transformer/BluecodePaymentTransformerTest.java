package com.example.bluecodepay.bluecode.transformer;

import backend.template.dto.ResponseStatus;
import backend.template.entity.Status;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.dto.BluecodeResponseDto;
import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import org.junit.jupiter.api.Test;

import static com.example.bluecodepay.bluecode.test.constant.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BluecodePaymentTransformerTest {

    private final BluecodePaymentTransformer bluecodePaymentTransformer = new BluecodePaymentTransformer();

    @Test
    void shouldTransformFromDto() {
        BluecodePayment payment = BluecodePayment.builder()
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.PENDING)
                .withId(PAYMENT_ID)
                .build();
        BluecodePaymentDto bluecodePaymentDto = BluecodePaymentDto.builder()
                .withAmount(AMOUNT)
                .withId(PAYMENT_ID)
                .build();
        assertEquals(payment, bluecodePaymentTransformer.fromDto(bluecodePaymentDto));
    }

    @Test
    void shouldTransformToPaymentResponseDto() {
        BluecodeResponseDto paymentDto = BluecodeResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.SUCCESS)
                .withAmount(AMOUNT)
                .withResult(ResponseStatus.OK)
                .withCurrency(CURRENCY)
                .build();
        BluecodePayment payment = BluecodePayment.builder()
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.SUCCESS)
                .withId(PAYMENT_ID)
                .build();
        assertEquals(paymentDto,
                bluecodePaymentTransformer.transformToPaymentResponseDto(payment));
    }

    @Test
    void shouldTransformToResponseDto() {
        BluecodeResponseDto bluecodeResponseDto = BluecodeResponseDto.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.SUCCESS)
                .withResult(ResponseStatus.OK)
                .build();
        BluecodePayment payment = BluecodePayment.builder()
                .withRequestedAmount(AMOUNT)
                .withStatus(Status.SUCCESS)
                .withId(PAYMENT_ID)
                .build();
        assertEquals(bluecodeResponseDto,
                bluecodePaymentTransformer.transformToResponseDto(payment));
    }
}