package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;
import ru.arman.bankingsystembackend.service.CustomerService;
import ru.arman.bankingsystembackend.service.NotificationService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    @Override
    @Scheduled(cron = "0 0 10 1 * *")
    public void checkIsPremium() {
        customerRepository.findAllByCustomerType(CustomerType.PREMIUM)
                .stream().filter(c -> !isPremium().test(c))
                .forEach(c -> {
                    c.setCustomerType(CustomerType.REGULAR);
                    notificationService.sendEmail(c, "Account type",
                            "Unfortunately, you no longer meet the requirements of a premium customer.");
                });

        customerRepository.findAllByCustomerType(CustomerType.REGULAR)
                .stream().filter(c -> isPremium().test(c))
                .forEach(c -> {
                    c.setCustomerType(CustomerType.PREMIUM);
                    notificationService.sendEmail(c, "Account type",
                            "You are premium customer now.");
                });
    }

    /**
     * Checks if account is premium or not
     *  - Сумма покупок от 200 000 ₽ в месяц и баланс на счетах от 1 000 000 ₽
     *  - Общий остаток на счетах от 3 000 000 ₽
     *  - На счета приходит от 400 000 ₽ в месяц
     *
     * @return true if it's premium or false otherwise
     */
    private Predicate<Customer> isPremium() {
        return customer -> {
            Instant oneMonthAgoInstance = Instant.now()
                    .minus(Duration.ofDays(30));
            Date oneMonthAgoDate = Date.from(oneMonthAgoInstance);

            Supplier<Stream<Account>> accounts =
                    () -> customer.getAccounts().stream().filter(a -> a.getAccountType().equals(AccountType.CHECKING));

            return accounts.get()
                    .map(a -> transactionRepository.findByAccountAndTransactionDateGreaterThan(a, oneMonthAgoDate))
                    .flatMap(List::stream)
                    .filter(t -> t.getTransactionType().equals(TransactionType.PURCHASE))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(new BigDecimal(200_000)) > 0
                    &&
                    accounts.get().map(Account::getCurrentBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(new BigDecimal(1_000_000)) > 0
                    ||
                    accounts.get().map(Account::getCurrentBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(new BigDecimal(3_000_000)) > 0
                    ||
                    accounts.get()
                    .map(a -> transactionRepository.findByAccountAndTransactionDateGreaterThan(a, oneMonthAgoDate))
                    .flatMap(List::stream)
                    .filter(t -> t.getTransactionType().equals(TransactionType.DEPOSIT))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(new BigDecimal(400_000)) > 0;

        };
    }
}
