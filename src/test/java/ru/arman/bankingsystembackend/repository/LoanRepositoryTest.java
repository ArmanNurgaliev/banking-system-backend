package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanRepositoryTest {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private PersonRepository personRepository;

    private Customer customer;
    private Loan loan1;
    private Loan loan2;

    @BeforeEach
    void setUp() {
        Person person = Person.builder()
                .id(1L)
                .email("some@mail.ru")
                .password("pass")
                .firstName("name")
                .lastName("surname")
                .phoneNumber("12345789")
                .role(Role.CUSTOMER)
                .build();

        customer = new Customer();
        customer.setCustomerType(CustomerType.REGULAR);
        customer.setPerson(person);
        personRepository.save(person);

        loan1 = Loan.builder()
                .loanType(LoanType.PERSONAL)
                .loanAmount(BigDecimal.valueOf(10000))
                .customer(customer)
                .startDate(new Date(System.currentTimeMillis()))
                .endDate(new Date(System.currentTimeMillis() + 10000))
                .term(18)
                .status(Status.ACTIVE)
                .build();
        loan1 = loanRepository.save(loan1);

        loan2 = Loan.builder()
                .loanType(LoanType.BUSINESS)
                .loanAmount(BigDecimal.valueOf(1000000))
                .customer(customer)
                .startDate(new Date(System.currentTimeMillis()))
                .endDate(new Date(System.currentTimeMillis() + 100000))
                .term(24)
                .status(Status.ACTIVE)
                .build();
        loan2 = loanRepository.save(loan2);
    }

    @Test
    void findAllByCustomerTest_shouldReturnLoans() {
        List<Loan> loans = loanRepository.findAllByCustomer(customer);

        assertThat(loans).hasSize(2).contains(loan1, loan2);
    }
}