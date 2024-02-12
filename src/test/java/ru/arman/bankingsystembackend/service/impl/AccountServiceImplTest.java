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
import ru.arman.bankingsystembackend.dto.AccountCreateDto;
import ru.arman.bankingsystembackend.dto.TransactionDto;
import ru.arman.bankingsystembackend.dto.TransferMoneyDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.exception.AccountNotFoundException;
import ru.arman.bankingsystembackend.exception.AccountViolationException;
import ru.arman.bankingsystembackend.exception.NotEnoughMoneyException;
import ru.arman.bankingsystembackend.repository.AccountRepository;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;
import ru.arman.bankingsystembackend.service.PersonService;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestContainerConfiguration.class)
class AccountServiceImplTest {
    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private BranchServiceImpl branchService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PersonService personService;
    private Account account;
    private Account account1;
    private Branch branch;
    private TransactionDto transactionDto;
    private AccountCreateDto accountCreateDto;
    private Person person;
    private Customer customer;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        BranchAddress branchAddress = BranchAddress.builder()
                .city("Tomsk")
                .street("Lenina")
                .house("5")
                .build();
        branch = Branch.builder()
                .id(1L)
                .name("Main")
                .phoneNumber("494949")
                .balance(BigDecimal.valueOf(0))
                .bic("2569845")
                .build();
        branch.setAddress(branchAddress);

        person = Person.builder().build();

        account = Account.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .accountNumber("26589563259745123059")
                .currentBalance(BigDecimal.valueOf(0, 2))
                .dateOpened(new Date(System.currentTimeMillis()))
                .accountStatus(AccountStatus.OPEN)
                .customers(new HashSet<>())
                .branch(branch)
                .build();

        account1 = Account.builder()
                .id(2L)
                .accountType(AccountType.CHECKING)
                .accountNumber("26589563259745123060")
                .currentBalance(BigDecimal.valueOf(100, 2))
                .dateOpened(new Date(System.currentTimeMillis()))
                .accountStatus(AccountStatus.OPEN)
                .customers(new HashSet<>())
                .branch(branch)
                .build();

        customer = new Customer();
        customer.setId(1L);
        customer.setAccounts(new ArrayList<>());
        customer.getAccounts().addAll(List.of(account, account1));
        customer.setPerson(person);

        transactionDto = new TransactionDto();
        transactionDto.setAccountId(account.getId());
        transactionDto.setSum(BigDecimal.valueOf(100, 2));

        accountCreateDto = new AccountCreateDto();
        accountCreateDto.setAccountType("CHECKING");
        accountCreateDto.setBranchId(branch.getId());

        auth = new UsernamePasswordAuthenticationToken(person, "pass");
    }

    @Test
    void getAccountByIdTest_shouldReturnAccount() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        Account accountById = accountService.getAccountById(account.getId());

        assertEquals(account, accountById);
    }

    @Test
    void getAccountByIdTest_shouldThrowException() {
        AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(account.getId()));

        assertEquals("Account not found with id " + account.getId(), accountNotFoundException.getMessage());
    }

    @Test
    void createAccountTest_shouldReturnAccount() {
        when(branchService.getBranchById(any())).thenReturn(branch);
        when(accountRepository.save(any())).thenReturn(account);
        when(personService.findByEmail(any())).thenReturn(person);

        Account createdAccount = accountService.createAccount(accountCreateDto, auth);

        assertEquals(account, createdAccount);
    }

    @Test
    void getAllAccountsTest_shouldReturnAccount() {
        when(personService.findByEmail(any())).thenReturn(person);

        List<Account> allAccount = accountService.getAllAccount(auth);

        assertEquals(2, allAccount.size());
    }

    @Test
    void transferMoneyTest_shouldTransferMoney() {
        TransferMoneyDto transferMoneyDto = new TransferMoneyDto(2L, 1L, BigDecimal.valueOf(1));

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(personService.findByEmail(any())).thenReturn(person);

        String response = accountService.transferMoney(transferMoneyDto, auth);

        assertEquals("You send " + transferMoneyDto.getSum() + " to: " + account.getAccountNumber(),
                response);

        assertEquals(BigDecimal.valueOf(0, 2), account1.getCurrentBalance());
    }

    @Test
    void transferMoneyTest_shouldThrowNotEnoughMoneyException() {
        TransferMoneyDto transferMoneyDto = new TransferMoneyDto(1L, 1L, BigDecimal.valueOf(1));

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(personService.findByEmail(any())).thenReturn(person);

        NotEnoughMoneyException exception = assertThrows(NotEnoughMoneyException.class,
                () -> accountService.transferMoney(transferMoneyDto, auth));

        assertEquals("You don't have enough money", exception.getMessage());
    }

    @Test
    void transferMoneyTest_shouldThrowAccountViolationException() {
        TransferMoneyDto transferMoneyDto = new TransferMoneyDto(1L, 1L, BigDecimal.valueOf(1));
        Person person1 = new Person();
        person1.setCustomer(new Customer());

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(personService.findByEmail(any())).thenReturn(person1);

        AccountViolationException exception = assertThrows(AccountViolationException.class,
                () -> accountService.transferMoney(transferMoneyDto, auth));

        assertEquals("You can't withdraw money from this account", exception.getMessage());
    }

    @Test
    void withdrawMoneyTest_shouldWithdrawMoney() {
        transactionDto.setAccountId(account1.getId());
        when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        when(personService.findByEmail(any())).thenReturn(person);

        String response = accountService.withdrawMoney(transactionDto, auth);

        assertEquals("You withdrew " + transactionDto.getSum() + " from account: " + account1.getAccountNumber(),
                response);
        assertEquals(BigDecimal.valueOf(0, 2), account1.getCurrentBalance());
    }

    @Test
    void depositMoneyTest_shouldDepositMoney() {
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        accountService.depositMoney(transactionDto);

        assertEquals(transactionDto.getSum(), account.getCurrentBalance());
    }
}