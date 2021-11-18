package backend.service.integration.configutation;

import backend.service.test.bean.TestPayment;
import backend.service.test.bean.TestPaymentDto;
import backend.service.test.bean.TestPaymentResponseDto;
import backend.service.test.constant.TestConstants;
import backend.template.config.SubtypesInformationHolder;
import backend.template.provider.PaymentProvider;
import backend.template.transformer.PaymentTransformer;
import com.example.bluecodepay.bluecode.dto.BluecodePaymentDto;
import com.example.bluecodepay.bluecode.dto.BluecodeResponseDto;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.BDDMockito.given;

@TestConfiguration
public class TestConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public PaymentProvider<TestPaymentDto, TestPayment> testPaymentProvider() {
        PaymentProvider<TestPaymentDto, TestPayment> paymentProvider =
                (PaymentProvider<TestPaymentDto, TestPayment>) Mockito.mock(PaymentProvider.class);
        given(paymentProvider.getType()).willReturn(TestConstants.PAYMENT_TYPE);
        return paymentProvider;
    }


    @Bean
    @SuppressWarnings("unchecked")
    public PaymentTransformer<TestPayment, TestPaymentResponseDto> testPaymentTransformer() {
        PaymentTransformer<TestPayment, TestPaymentResponseDto> paymentTransformer =
                (PaymentTransformer<TestPayment, TestPaymentResponseDto>) Mockito.mock(PaymentTransformer.class);
        given(paymentTransformer.getType()).willReturn(TestConstants.PAYMENT_TYPE);
        return paymentTransformer;
    }

    @Bean
    public SubtypesInformationHolder testSubtypes(){
        return new SubtypesInformationHolder(List.of(TestPaymentDto.class, TestPaymentResponseDto.class));
    }


}
