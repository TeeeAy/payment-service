package backend.service.test.bean;

import backend.service.test.constant.TestConstants;
import backend.template.dto.PaymentDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder(setterPrefix = "with", toBuilder = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestPaymentDto extends PaymentDto {

    @Override
    @JsonIgnore
    public String getType() {
        return TestConstants.PAYMENT_TYPE;
    }
}
