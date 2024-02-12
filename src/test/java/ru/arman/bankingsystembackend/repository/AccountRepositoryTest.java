package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Account;
import ru.arman.bankingsystembackend.entity.AccountStatus;
import ru.arman.bankingsystembackend.entity.AccountType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByAccountNumberTest_shouldReturnAccount() {
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .accountStatus(AccountStatus.OPEN)
                .accountNumber("12345498")
                .currentBalance(BigDecimal.valueOf(100000))
                .build();
        accountRepository.save(account);

        Account byAccountNumber = accountRepository.findByAccountNumber(account.getAccountNumber()).get();

        assertEquals(entityManager.find(Account.class, account.getId()), byAccountNumber);
    }

    @Test
    void findByAccountNumberTest_shouldReturnOptionalEmpty() {
        Optional<Account> account = accountRepository.findByAccountNumber("12345498");

        assertThat(account).isEmpty();
    }
}