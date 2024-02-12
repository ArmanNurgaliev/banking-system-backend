package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Account;
import ru.arman.bankingsystembackend.entity.AccountStatus;
import ru.arman.bankingsystembackend.entity.AccountType;
import ru.arman.bankingsystembackend.entity.Transaction;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    private Account account;
    private Account account2;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountNumber("123546789456789456")
                .accountType(AccountType.CHECKING)
                .accountStatus(AccountStatus.OPEN)
                .currentBalance(BigDecimal.valueOf(200000))
                .build();
        account = accountRepository.save(account);

        account2 = Account.builder()
                .accountNumber("64985618166")
                .accountType(AccountType.CD)
                .accountStatus(AccountStatus.OPEN)
                .currentBalance(BigDecimal.valueOf(200000))
                .build();
        account2 = accountRepository.save(account2);

        transaction1 = new Transaction();
        transaction1.setTransactionDate(Date.valueOf("2024-02-01"));
        transaction1.setAccount(account);
        transaction1 = transactionRepository.save(transaction1);

        transaction2 = new Transaction();
        transaction2.setTransactionDate(Date.valueOf("2024-03-01"));
        transaction2.setAccount(account);
        transaction2 = transactionRepository.save(transaction2);
    }

    @Test
    void findByAccountAndTransactionDateTest_shouldReturnTransactions() {
        List<Transaction> transactions =
                transactionRepository.findByAccountAndTransactionDateGreaterThan(account, Date.valueOf("2024-01-01"));

        assertThat(transactions).hasSize(2).contains(transaction1, transaction2);
    }

    @Test
    void findByAccountAndTransactionDateTest_shouldReturnEmptyList() {
        List<Transaction> transactions =
                transactionRepository.findByAccountAndTransactionDateGreaterThan(account2, Date.valueOf("2024-01-01"));

        assertThat(transactions).isEmpty();
    }
}