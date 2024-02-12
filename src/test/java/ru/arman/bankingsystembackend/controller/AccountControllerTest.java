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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.AccountCreateDto;
import ru.arman.bankingsystembackend.dto.TransactionDto;
import ru.arman.bankingsystembackend.dto.TransferMoneyDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.repository.AccountRepository;
import ru.arman.bankingsystembackend.repository.BranchRepository;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.PersonRepository;
import ru.arman.bankingsystembackend.service.PersonService;
import ru.arman.bankingsystembackend.service.impl.JwtService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@Transactional
class AccountControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PersonService personService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BranchRepository branchRepository;

    private String token;
    private Person person;
    private Customer customer;
    private Account account1;
    private Account account2;
    private Branch branch;
    private ObjectMapper objectMapper;
    @Autowired
    private PersonRepository personRepository;

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

        account1 = Account.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .accountStatus(AccountStatus.OPEN)
                .accountNumber("12345498")
                .currentBalance(BigDecimal.valueOf(100000))
                .branch(branch)
                .build();
        account2 = Account.builder()
                .id(2L)
                .accountType(AccountType.SAVINGS)
                .accountStatus(AccountStatus.OPEN)
                .accountNumber("456189451")
                .branch(branch)
                .currentBalance(BigDecimal.valueOf(100000))
                .build();
        account1 = accountRepository.save(account1);
        account2 = accountRepository.save(account2);

        person = Person.builder()
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
        customer.setAccounts(List.of(account1, account2));
        customer = customerRepository.save(customer);

        objectMapper = new ObjectMapper();

        token = "Bearer " + jwtService.generateAccessToken(new SecuredPerson(person));
    }

    @Test
    void createAccountTest_shouldReturnCreatedAccount() throws Exception {
        customer.setAccounts(new ArrayList<>());
        customer = customerRepository.save(customer);
        AccountCreateDto accountCreateDto = new AccountCreateDto("CHECKING", branch.getId());

        mockMvc.perform(post("/api/accounts/create")
                .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value(accountCreateDto.getAccountType()));
    }

    @Test
    void createAccountTest_shouldThrowBranchNotFoundException() throws Exception {
        AccountCreateDto accountCreateDto = new AccountCreateDto("CHECKING", 11L);

        mockMvc.perform(post("/api/accounts/create")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Branch not found with id: " + accountCreateDto.getBranchId()));
    }

    @Test
    void getAccountByIdTest_shouldReturnAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/my/" + account1.getId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(account1.getAccountNumber()));
    }

    @Test
    void getAccountByIdTest_shouldThrowAccountNotFoundException() throws Exception {
        long accId = 3L;
        mockMvc.perform(get("/api/accounts/my/" + accId)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found with id " + accId));
    }

    @Test
    void getAllAccountsTest_shouldReturnAccounts() throws Exception {
        mockMvc.perform(get("/api/accounts/all")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void transferMoneyTest_shouldReturnMessage() throws Exception {
        TransferMoneyDto transferMoneyDto = new TransferMoneyDto(account1.getId(), account2.getId(), BigDecimal.valueOf(5000));
        BigDecimal firstAccBalance = account1.getCurrentBalance().subtract(transferMoneyDto.getSum());
        BigDecimal secAccBalance = account2.getCurrentBalance().add(transferMoneyDto.getSum());

        mockMvc.perform(post("/api/accounts/transfer")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferMoneyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("You send " + transferMoneyDto.getSum() + " to: " + account2.getAccountNumber()));

        assertEquals(firstAccBalance, account1.getCurrentBalance());
        assertEquals(secAccBalance, account2.getCurrentBalance());
    }

    @Test
    void transferMoneyTest_shouldThrowAccountValidationException() throws Exception {
        TransferMoneyDto transferMoneyDto = new TransferMoneyDto(account1.getId(), account2.getId(), BigDecimal.valueOf(5000));
        person.getCustomer().setAccounts(new ArrayList<>());

        mockMvc.perform(post("/api/accounts/transfer")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferMoneyDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You can't withdraw money from this account"));
    }

    @Test
    void withdrawMoneyTest_shouldReturnMessage() throws Exception {
        TransactionDto transactionDto = new TransactionDto(account1.getId(), BigDecimal.valueOf(5000));
        BigDecimal accBalance = account1.getCurrentBalance().subtract(transactionDto.getSum());

        mockMvc.perform(post("/api/accounts/withdraw")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("You withdrew " + transactionDto.getSum() + " from account: " + account1.getAccountNumber()));

        assertEquals(accBalance, account1.getCurrentBalance());
    }

    @Test
    void withdrawMoneyTest_shouldThrowAccountValidationException() throws Exception {
        TransactionDto transactionDto = new TransactionDto(account1.getId(), BigDecimal.valueOf(5000));
        person.getCustomer().setAccounts(new ArrayList<>());

        mockMvc.perform(post("/api/accounts/withdraw")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You can't withdraw money from this account"));
    }

    @Test
    void withdrawMoneyTest_shouldThrowNotEnoughMoneyException() throws Exception {
        TransactionDto transactionDto = new TransactionDto(account1.getId(), account1.getCurrentBalance().add(BigDecimal.valueOf(100)));

        mockMvc.perform(post("/api/accounts/withdraw")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("You don't have enough money"));
    }

    @Test
    void depositMoneyTest_shouldReturnMessage() throws Exception {
        TransactionDto transactionDto = new TransactionDto(account1.getId(), BigDecimal.valueOf(5000));
        BigDecimal accBalance = account1.getCurrentBalance().add(transactionDto.getSum());

        mockMvc.perform(post("/api/accounts/deposit")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("You transferred " + transactionDto.getSum() + " to account: " + account1.getAccountNumber()));

        assertEquals(accBalance, account1.getCurrentBalance());
    }
}