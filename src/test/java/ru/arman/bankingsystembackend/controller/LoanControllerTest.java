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
import ru.arman.bankingsystembackend.dto.LoanDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.repository.BranchRepository;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.PersonRepository;
import ru.arman.bankingsystembackend.service.impl.JwtService;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@Transactional
class LoanControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private Branch branch;
    private Customer customer;
    private String token;
    private Loan loan;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void initData() {
        branch = Branch.builder()
                .id(1L)
                .bic("1234567")
                .balance(BigDecimal.valueOf(100000000))
                .name("Main")
                .phoneNumber("45684612")
                .build();
        branch = branchRepository.save(branch);

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
        customer.setAccounts(new ArrayList<>());
        customer.setPerson(person);
        personRepository.save(person);

        objectMapper = new ObjectMapper();
        token = "Bearer " + jwtService.generateAccessToken(new SecuredPerson(person));
    }

    @Test
    void applyForALoanTest_shouldReturnPayments() throws Exception {
        LoanDto loanDto = new LoanDto("PERSONAL", BigDecimal.valueOf(100000), 5,
                new Date(System.currentTimeMillis()), branch.getId(), customer.getId());

        mockMvc.perform(post("/api/loans/apply")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(loanDto.getTerm()));
    }

    @Test
    @Sql("/insert_payments.sql")
    void getAllLoansTest_shouldReturnLoanInfo() throws Exception {
        Person person = personRepository.findByEmail("email@mail.ru").get();
        String accessToken = "Bearer " + jwtService.generateAccessToken(new SecuredPerson(person));

        mockMvc.perform(get("/api/loans/my")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].term").value(12));
    }
}