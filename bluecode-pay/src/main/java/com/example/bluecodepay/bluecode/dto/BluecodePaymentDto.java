package com.example.bluecodepay.bluecode.dto;


import backend.template.dto.PaymentDto;
import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("bluecode")
public class BluecodePaymentDto extends PaymentDto {

    @JsonIgnore
    public String getType(){
        return "bluecode";
    }

}
