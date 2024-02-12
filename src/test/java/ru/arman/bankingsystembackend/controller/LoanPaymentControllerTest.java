package ru.arman.bankingsystembackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.LoanPaymentDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.repository.AccountRepository;
import ru.arman.bankingsystembackend.repository.BranchRepository;
import ru.arman.bankingsystembackend.repository.PersonRepository;
import ru.arman.bankingsystembackend.service.impl.JwtService;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerConfiguration.class)
@Transactional
@ActiveProfiles("test")
@Sql("/insert_payments.sql")
class LoanPaymentControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private AccountRepository accountRepository;

    private String token;
    private Account account;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void initData() {
        Person person = personRepository.findByEmail("email@mail.ru").get();

        Branch branch = Branch.builder()
                .name("Main")
                .bic("1325486")
                .phoneNumber("545612894")
                .balance(BigDecimal.valueOf(10000000))
                .build();
        branchRepository.save(branch);

        account = Account.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .accountNumber("54612354484516554")
                .currentBalance(BigDecimal.valueOf(500000))
                .branch(branch)
                .build();
        accountRepository.save(account);

        objectMapper = new ObjectMapper();
        token = "Bearer " + jwtService.generateAccessToken(new SecuredPerson(person));
    }

    @Test
    void getAllLoanPaymentsTest_shouldReturnPayments() throws Exception {
        mockMvc.perform(get("/api/payments/" + 1L)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(12))
                .andExpect(jsonPath("$[0].paymentAmount").value(26374.77));
    }

    @Test
    void payLoanPaymentTest_shouldReturnMessage() throws Exception {
        LoanPaymentDto loanPaymentDto = new LoanPaymentDto(1L, account.getId(), BigDecimal.valueOf(26374.77));

        mockMvc.perform(post("/api/payments/pay")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanPaymentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Loan payment of " + loanPaymentDto.getSum() +
                                " paid with account: " + account.getAccountNumber() + ". Remains to be paid: " +
                                BigDecimal.valueOf(0, 2)));
    }
}