package backend.template.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@NoArgsConstructor
@Data
@SuperBuilder(setterPrefix = "with", toBuilder = true)
public abstract class PaymentDto {

    public abstract String getType();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String barcode;

    private int amount;

    private String currencyCode;


}
