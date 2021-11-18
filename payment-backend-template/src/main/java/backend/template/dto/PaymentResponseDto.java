package backend.template.dto;


import backend.template.entity.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("type")
public abstract class PaymentResponseDto {

    public abstract String getType();

    @Enumerated(EnumType.STRING)
    private ResponseStatus result;

    private String id;

    private Status status;

    private Integer amount;

    private String currency;


}
