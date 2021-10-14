package com.example.bluecodepay.bluecode.config;


import backend.template.config.SubtypesInformationHolder;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.dto.BluecodeResponseDto;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@EntityScan("com.example.bluecodepay.bluecode.entity")
@EnableFeignClients(basePackages = "com.example.bluecodepay.bluecode.client")
@Configuration
@EnableHystrix
@EnableDiscoveryClient
@ComponentScan("com.example.bluecodepay.bluecode")
public class BluecodeConfig {

    @Bean
    @ConditionalOnProperty(name = {
            "bluecode.api.username", "bluecode.api.password"
    })
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(
            @Value("${bluecode.api.username}") String username,
            @Value("${bluecode.api.password}") String password) {
        return new BasicAuthRequestInterceptor(username, password);
    }

    @Bean
    public SubtypesInformationHolder bluecodeSubtypes(){
        return new SubtypesInformationHolder(List.of(BluecodePaymentDto.class, BluecodeResponseDto.class));
    }

}
