package backend.service.test.bean;

import backend.service.test.constant.TestConstants;
import backend.template.entity.Payment;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;


@SuppressWarnings({"LombokEqualsAndHashCodeInspection", "LombokDataInspection"})
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class TestPayment extends Payment {

    @Override
    public String getType() {
        return TestConstants.PAYMENT_TYPE;
    }
}
