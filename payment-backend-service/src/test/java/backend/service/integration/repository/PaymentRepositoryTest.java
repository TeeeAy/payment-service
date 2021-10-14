package backend.service.integration.repository;

import backend.service.container.PostgreSQLTestContainer;
import backend.service.repository.PaymentRepository;
import backend.service.test.bean.TestPayment;
import backend.template.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static backend.service.test.constant.TestConstants.ABSENT_PAYMENT_ID;
import static backend.service.test.constant.TestConstants.PAYMENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@EntityScan("backend.service.test.bean")
@Sql(value = "/sql/dataPaymentInsert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/dataPaymentDelete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@PostgreSQLTestContainer
@DirtiesContext
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void shouldFindPaymentById() {
        TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.PENDING)
                .build();
        assertEquals(Optional.of(testPayment), paymentRepository.findById(PAYMENT_ID));
    }


    @Test
    public void shouldNotFindPaymentById() {
        assertEquals(Optional.empty(), paymentRepository.findById(ABSENT_PAYMENT_ID));
    }


    @Test
    public void shouldSavePayment() {
        TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.PENDING)
                .build();
        TestPayment savedPayment = paymentRepository.save(testPayment);
        assertEquals(testPayment, savedPayment);
    }

    @Test
    public void shouldSaveAndFlushPayment() {
        TestPayment testPayment = TestPayment.builder()
                .withId(PAYMENT_ID)
                .withStatus(Status.PENDING)
                .build();
        TestPayment savedPayment = paymentRepository.saveAndFlush(testPayment);
        assertEquals(testPayment, savedPayment);
    }

}