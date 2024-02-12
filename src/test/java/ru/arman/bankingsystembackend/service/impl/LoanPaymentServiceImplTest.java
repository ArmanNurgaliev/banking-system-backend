package ru.arman.bankingsystembackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.LoanPaymentDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.repository.LoanPaymentRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestContainerConfiguration.class)
class LoanPaymentServiceImplTest {

    @InjectMocks
    private LoanPaymentServiceImpl loanPaymentService;

    @Mock
    private LoanPaymentRepository loanPaymentRepository;
    @Mock
    private AccountServiceImpl accountService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private PersonServiceImpl personService;
    @Mock
    private LoanServiceImpl loanService;

    private LoanPayment loanPayment1;
    private LoanPayment loanPayment2;
    private Account account;
    private Branch branch;
    private Authentication auth;
    private Person person;
    private Customer customer;
    private Loan loan;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1L)
                .email("some@mail.ru")
                .password("pass")
                .firstName("name")
                .lastName("surname")
                .phoneNumber("12345789")
                .role(Role.CUSTOMER)
                .build();

        customer = new Customer();
        customer.setId(1L);
        customer.setAccounts(new ArrayList<>());
        customer.setPerson(person);

        loan = new Loan();
        loan.setCustomer(customer);

        loanPayment1 = LoanPayment.builder()
                .id(1L)
                .scheduledPaymentDate(new Date(System.currentTimeMillis()))
                .paymentAmount(BigDecimal.valueOf(1000))
                .principalAmount(BigDecimal.valueOf(900))
                .interestAmount(BigDecimal.valueOf(100))
                .paidAmount(BigDecimal.valueOf(1000))
                .loan(loan)
                .residualAmount(BigDecimal.valueOf(100000))
                .build();

        loanPayment2 = LoanPayment.builder()
                .id(2L)
                .scheduledPaymentDate(new Date(System.currentTimeMillis()))
                .paymentAmount(BigDecimal.valueOf(1000))
                .principalAmount(BigDecimal.valueOf(900))
                .interestAmount(BigDecimal.valueOf(100))
                .paidAmount(BigDecimal.valueOf(1000))
                .loan(loan)
                .residualAmount(BigDecimal.valueOf(100000))
                .build();

        branch = Branch.builder()
                .id(1L)
                .name("Name")
                .phoneNumber("123456789")
                .balance(BigDecimal.valueOf(10000))
                .build();

        account = Account.builder()
                .id(1L)
                .accountStatus(AccountStatus.OPEN)
                .accountType(AccountType.CHECKING)
                .accountNumber("12345678901234567890")
                .currentBalance(BigDecimal.valueOf(100000))
                .branch(branch)
                .build();


        auth = new UsernamePasswordAuthenticationToken(person, "pass");
    }

    @Test
    void getPaymentsTest_shouldReturnPayments() {
        when(loanService.getById(any())).thenReturn(loan);
        when(personService.findByEmail(any())).thenReturn(person);
        when(loanPaymentRepository.findByLoan(any())).thenReturn(List.of(loanPayment1, loanPayment2));

        List<LoanPayment> payments = loanPaymentService.getPayments(1L, auth);

        assertEquals(2, payments.size());
    }

    @Test
    void payLoanPaymentTest_shouldReturnResponse() {
        LoanPaymentDto loanPaymentDto = new LoanPaymentDto(1L ,1L, BigDecimal.valueOf(1000));

        when(accountService.getAccountById(any())).thenReturn(account);
        when(loanPaymentRepository.findById(any())).thenReturn(Optional.of(loanPayment1));
        when(loanPaymentRepository.save(any())).thenReturn(loanPayment1);

        String response = loanPaymentService.payLoanPayment(loanPaymentDto);

        assertEquals("Loan payment of " + loanPaymentDto.getSum() +
                " paid with account: " + account.getAccountNumber() + ". Remains to be paid: " +
                BigDecimal.ZERO, response);
    }
}