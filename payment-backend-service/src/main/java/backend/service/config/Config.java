package backend.service.config;

import backend.template.config.SubtypesInformationHolder;
import backend.template.dto.PaymentDto;
import backend.template.dto.PaymentResponseDto;
import backend.template.entity.Payment;
import backend.template.handler.HystrixExceptionHandler;
import backend.template.provider.PaymentProvider;
import backend.template.transformer.PaymentTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class Config {

    private final List<SubtypesInformationHolder> subtypesInformationHolders;

    @Autowired
    public  Config(List<SubtypesInformationHolder> subtypesInformationHolders){
        this.subtypesInformationHolders = subtypesInformationHolders;
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        Class<?>[] subtypesToRegister = subtypesInformationHolders.stream()
                .map(SubtypesInformationHolder::getSubTypes)
                .flatMap(List::stream)
                .toArray(Class<?>[]::new);
        mapper.registerSubtypes(subtypesToRegister);
        return mapper;
    }


    @Bean
    public <T extends PaymentDto, R extends Payment> Map<String, PaymentProvider<T,R>> serviceLogicMap(
            List<PaymentProvider<T,R>> paymentProviders
    ){
        return paymentProviders.stream()
                .collect(Collectors.toMap(PaymentProvider::getType, Function.identity()));
    }

    @Bean
    public <T extends Payment, R extends PaymentResponseDto> Map<String, PaymentTransformer<T,R>> transformerMap(
            List<PaymentTransformer<T,R>> paymentProviders
    ){
        return paymentProviders.stream()
                .collect(Collectors.toMap(PaymentTransformer::getType, Function.identity()));
    }


    @Bean
    public <T extends Throwable> List<HystrixExceptionHandler<T>> exceptionHandlersList(
            List<HystrixExceptionHandler<T>> exceptionHandlers
    ){
        return exceptionHandlers;
    }


}
