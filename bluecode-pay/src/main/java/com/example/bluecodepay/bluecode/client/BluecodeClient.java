package com.example.bluecodepay.bluecode.client;

import com.example.bluecodepay.bluecode.entity.BluecodePayment;
import com.example.bluecodepay.bluecode.entity.BluecodeRefund;
import com.example.bluecodepay.bluecode.response.BluecodeResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "bluecode",
        url = "${bluecode.api.url}", fallbackFactory = BluecodeClientFallback.class)
public interface BluecodeClient {

    @Headers("Content-Type: application/json")
    @RequestMapping(method = RequestMethod.POST, value = "/payment")
    ResponseEntity<BluecodeResponse> startPayment(@RequestBody BluecodePayment payment);

    @Headers("Content-Type: application/json")
    @RequestMapping(method = RequestMethod.POST, value = "/status")
    BluecodeResponse checkStatus(@RequestBody BluecodePayment payment);

    @Headers("Content-Type: application/json")
    @RequestMapping(method = RequestMethod.POST, value = "/cancel")
    ResponseEntity<BluecodeResponse> cancelPayment(@RequestBody BluecodePayment payment);

    @Headers("Content-Type: application/json")
    @RequestMapping(method = RequestMethod.POST, value = "/refund")
    ResponseEntity<BluecodeResponse> refundPayment(@RequestBody BluecodeRefund refund);
}

