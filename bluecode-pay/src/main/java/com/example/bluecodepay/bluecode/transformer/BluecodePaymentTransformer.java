package com.example.bluecodepay.bluecode.transformer;

import backend.template.dto.ResponseStatus;
import backend.template.transformer.PaymentTransformer;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.dto.BluecodeResponseDto;
import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import org.springframework.stereotype.Component;


@Component
public class BluecodePaymentTransformer implements PaymentTransformer<BluecodePayment, BluecodeResponseDto> {

    public BluecodePayment fromDto(BluecodePaymentDto bluecodePaymentDto) {
        return BluecodePayment.builder()
                .withBarcode(bluecodePaymentDto.getBarcode())
                .withRequestedAmount(bluecodePaymentDto.getAmount())
                .withId(bluecodePaymentDto.getId())
                .build();
    }


    public BluecodeResponseDto transformToPaymentResponseDto(BluecodePayment bluecodePayment) {
        return BluecodeResponseDto.builder()
                .withId(bluecodePayment.getMerchantTxId())
                .withResult(ResponseStatus.OK)
                .withStatus(bluecodePayment.getStatus())
                .withCurrency(bluecodePayment.getCurrency())
                .withAmount(bluecodePayment.getRequestedAmount())
                .build();
    }


    public BluecodeResponseDto transformToResponseDto(BluecodePayment bluecodePayment) {
        return BluecodeResponseDto.builder()
                .withId(bluecodePayment.getMerchantTxId())
                .withResult(ResponseStatus.OK)
                .withStatus(bluecodePayment.getStatus())
                .build();
    }


    @Override
    public String getType() {
        return "bluecode";
    }
}
