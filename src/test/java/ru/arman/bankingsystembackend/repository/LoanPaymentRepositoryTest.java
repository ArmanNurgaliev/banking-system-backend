package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Loan;
import ru.arman.bankingsystembackend.entity.LoanPayment;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanPaymentRepositoryTest {
    @Autowired
    private LoanPaymentRepository loanPaymentRepository;

    private Loan loan;
    private LoanPayment loanPayment1;
    private LoanPayment loanPayment2;
    private LoanPayment loanPayment3;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        loan.setLoanAmount(BigDecimal.valueOf(500000));
        loan.setStartDate(Date.valueOf("2024-01-01"));

        loanPayment1 = LoanPayment.builder()
                .id(1L)
                .scheduledPaymentDate(Date.valueOf("2024-01-01"))
                .paymentAmount(BigDecimal.valueOf(1000))
                .principalAmount(BigDecimal.valueOf(900))
                .interestAmount(BigDecimal.valueOf(100))
                .paidAmount(BigDecimal.valueOf(10))
                .loan(loan)
                .residualAmount(BigDecimal.valueOf(100000))
                .build();

        loanPayment2 = LoanPayment.builder()
                .id(2L)
                .scheduledPaymentDate(Date.valueOf("2024-02-01"))
                .paymentAmount(BigDecimal.valueOf(1000))
                .principalAmount(BigDecimal.valueOf(900))
                .interestAmount(BigDecimal.valueOf(100))
                .paidAmount(BigDecimal.valueOf(10))
                .loan(loan)
                .residualAmount(BigDecimal.valueOf(100000))
                .build();

        loanPayment3 = LoanPayment.builder()
                .id(3L)
                .scheduledPaymentDate(Date.valueOf("2024-02-01"))
                .paymentAmount(BigDecimal.valueOf(1000))
                .principalAmount(BigDecimal.valueOf(900))
                .interestAmount(BigDecimal.valueOf(100))
                .paidAmount(BigDecimal.valueOf(10))
                .residualAmount(BigDecimal.valueOf(100000))
                .build();

        loanPayment1 = loanPaymentRepository.save(loanPayment1);
        loanPayment2 = loanPaymentRepository.save(loanPayment2);
        loanPayment3 = loanPaymentRepository.save(loanPayment3);
    }

    @Test
    void findAllByScheduledPaymentDateTest_shouldReturnPayments() {
        List<LoanPayment> allPayments = loanPaymentRepository.findAllByScheduledPaymentDate(2024, 2);

        assertThat(allPayments).hasSize(2).contains(loanPayment2, loanPayment3);
    }

    @Test
    void findAllByScheduledPaymentDateTest_shouldReturnEmptyList() {
        List<LoanPayment> payments = loanPaymentRepository.findAllByScheduledPaymentDate(2024, 4);

        assertThat(payments).isEmpty();
    }

    @Test
    void findByLoanTest_shouldReturnPayments() {
        List<LoanPayment> payments = loanPaymentRepository.findByLoan(loan);

        assertThat(payments).hasSize(2).contains(loanPayment1, loanPayment2);
    }
}