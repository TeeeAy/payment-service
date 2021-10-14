package backend.service.test.bean;

import backend.service.test.constant.TestConstants;
import backend.template.dto.PaymentResponseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder(setterPrefix = "with", toBuilder = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TestPaymentResponseDto extends PaymentResponseDto {

    @Override
    public String getType() {
        return TestConstants.PAYMENT_TYPE;
    }
}
