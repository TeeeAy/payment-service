package com.example.bluecodepay.bluecode.dto;

import backend.template.dto.PaymentResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;


@SuperBuilder(setterPrefix = "with", toBuilder = true)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("type")
@AllArgsConstructor
public class BluecodeResponseDto extends PaymentResponseDto {

    @Override
    public String getType() {
        return "bluecode";
    }


}
