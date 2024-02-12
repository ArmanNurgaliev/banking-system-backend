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
import ru.arman.bankingsystembackend.dto.LoanDto;
import ru.arman.bankingsystembackend.dto.LoanInfoDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.LoanPaymentRepository;
import ru.arman.bankingsystembackend.repository.LoanRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestContainerConfiguration.class)
class LoanServiceImplTest {
    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountServiceImpl accountService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LoanPaymentRepository loanPaymentRepository;
    @Mock
    private PersonServiceImpl personService;
    @Mock
    private LoanRepository loanRepository;

    private Loan loan;
    private Customer customer;
    private Account account;
    private Branch branch;
    private Authentication auth;
    private Person person;
    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(1000000))
                .phoneNumber("123454879")
                .name("branch")
                .bic("45648494")
                .build();

        account = Account.builder()
                .id(1L)
                .branch(branch)
                .accountNumber("123546789456789456")
                .accountType(AccountType.CHECKING)
                .accountStatus(AccountStatus.OPEN)
                .currentBalance(BigDecimal.valueOf(200000))
                .build();

        customer = new Customer();
        customer.setId(1L);
        customer.setAccounts(List.of(account));
        customer.setCustomerType(CustomerType.REGULAR);

        loan = Loan.builder()
                .loanType(LoanType.PERSONAL)
                .loanAmount(BigDecimal.valueOf(10000))
                .customer(customer)
                .startDate(new Date(System.currentTimeMillis()))
                .endDate(new Date(System.currentTimeMillis() + 10000))
                .term(18)
                .status(Status.ACTIVE)
                .build();

        person = Person.builder().build();
        auth = new UsernamePasswordAuthenticationToken(person, "pass");
    }

    @Test
    void applyForACreditTest_shouldReturnPayments() {
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanType("PERSONAL");
        loanDto.setLoanAmount(BigDecimal.valueOf(10000));
        loanDto.setTerm(18);
        loanDto.setBranchId(1L);
        loanDto.setStartDate(new Date(System.currentTimeMillis()));

        when(customerRepository.getReferenceById(any())).thenReturn(customer);

        List<LoanPayment> payments = loanService.applyForACredit(loanDto, auth);

        assertEquals(loanDto.getTerm(), payments.size());
    }

    @Test
    void getAllLoansTest_shouldReturnLoanInfo() {
        when(personService.findByEmail(any())).thenReturn(person);
        when(loanRepository.findAllByCustomer(any())).thenReturn(List.of(loan));

        List<LoanInfoDto> response = loanService.getAllLoans("active", auth);

        assertEquals(1, response.size());
    }
}