package com.example.bluecodepay.bluecode.client;

import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import com.example.bluecodepay.bluecode.entity.BluecodeRefund;
import com.example.bluecodepay.bluecode.response.BluecodeResponse;
import com.example.bluecodepay.bluecode.service.BluecodeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BluecodeClientFallback implements FallbackFactory<BluecodeClient> {


    private final ObjectMapper objectMapper;


    public BluecodeClientFallback(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public BluecodeClient create(Throwable cause) {
        return new BluecodeClient() {

            @Override
            public ResponseEntity<BluecodeResponse> startPayment(BluecodePayment payment) {
                return ResponseEntity.ok().body(process());
            }

            @Override
            public BluecodeResponse checkStatus(BluecodePayment payment) {
                return null;
            }

            @Override
            public ResponseEntity<BluecodeResponse> cancelPayment(BluecodePayment payment) {
                BluecodeResponse response = process();
                throw new BluecodeException(response.getErrorCode().getStatusCode(),
                        response.getErrorCode().getErrorMessage());
            }

            @Override
            public ResponseEntity<BluecodeResponse> refundPayment(BluecodeRefund refund) {
                BluecodeResponse response = process();
                throw new BluecodeException(response.getErrorCode().getStatusCode(),
                        response.getErrorCode().getErrorMessage());
            }

            @Autowired
            private BluecodeResponse process() {
                try {
                    FeignException exception = (FeignException) cause;
                    return objectMapper.readValue(exception.contentUTF8(), BluecodeResponse.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
