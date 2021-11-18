package com.example.bluecodepay.bluecode.service;

import com.example.bluecodepay.bluecode.response.BluecodeResponse;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
public class RetryManager {

    @Builder(setterPrefix = "with", toBuilder = true)
    @Data
    public static class RetryInf{

        int maxAttempts;

        int waitDuration;

        @EqualsAndHashCode.Exclude
        Predicate<BluecodeResponse> condition;

        @EqualsAndHashCode.Exclude
        Supplier<BluecodeResponse> action;

    }

    public BluecodeResponse retryRequest(RetryInf retryInf){
        RetryConfig config = RetryConfig
                .<BluecodeResponse>custom()
                .waitDuration(Duration.ofMillis(retryInf.getWaitDuration()))
                .maxAttempts(retryInf.getMaxAttempts())
                .retryOnResult(retryInf.getCondition())
                .build();
        RetryRegistry retryRegistry = RetryRegistry.of(config);
        Retry retry = retryRegistry.retry("registry", config);
        return Retry.decorateSupplier(retry, retryInf.getAction()).get();
    }


}
