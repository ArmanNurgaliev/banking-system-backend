package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.arman.bankingsystembackend.dto.AccountCreateDto;
import ru.arman.bankingsystembackend.dto.TransactionDto;
import ru.arman.bankingsystembackend.dto.TransferMoneyDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.exception.AccountNotFoundException;
import ru.arman.bankingsystembackend.exception.AccountViolationException;
import ru.arman.bankingsystembackend.repository.AccountRepository;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;
import ru.arman.bankingsystembackend.service.AccountService;
import ru.arman.bankingsystembackend.service.BranchService;
import ru.arman.bankingsystembackend.service.PersonService;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BranchService branchService;
    private final PersonService personService;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id " + accountId));
    }

    @Override
    public Account createAccount(AccountCreateDto accountCreateDto, Authentication authentication) {
        Branch branch = branchService.getBranchById(accountCreateDto.getBranchId());

        Account account = Account.builder()
                .accountType(AccountType.valueOf(accountCreateDto.getAccountType().toUpperCase()))
                .accountNumber(generateAccountNumber())
                .currentBalance(BigDecimal.valueOf(0, 2))
                .dateOpened(new Date(System.currentTimeMillis()))
                .accountStatus(AccountStatus.OPEN)
                .customers(new HashSet<>())
                .branch(branch)
                .build();

        Account savedAccount = accountRepository.save(account);

        Person person = personService.findByEmail(authentication.getName());
        person.setRole(Role.CUSTOMER);

        Customer customer = person.getCustomer();

        if (customer == null) {
            customer = new Customer();
            customer.setCustomerType(CustomerType.REGULAR);
            customer.setPerson(person);
        }

        customer.addAccount(savedAccount);
        customerRepository.save(customer);

        return savedAccount;
    }

    @Override
    public List<Account> getAllAccount(Authentication authentication) {
        return personService.findByEmail(authentication.getName()).getCustomer().getAccounts();
    }

    @Override
    @Transactional
    public String transferMoney(TransferMoneyDto transferMoneyDto, Authentication authentication) {
        Person person = personService.findByEmail(authentication.getName());
        Account fromAccount = getAccountById(transferMoneyDto.getFromId());

        if (!person.getCustomer().getAccounts().contains(fromAccount))
            throw new AccountViolationException("You can't withdraw money from this account");

        Account toAccount = getAccountById(transferMoneyDto.getToId());

        fromAccount.withdraw(transferMoneyDto.getSum());
        toAccount.deposit(transferMoneyDto.getSum());

        Transaction transaction1 = new Transaction(transferMoneyDto.getSum().negate(), fromAccount, TransactionType.TRANSFER, null);
        Transaction transaction2 = new Transaction(transferMoneyDto.getSum(), toAccount, TransactionType.TRANSFER, null);
        transaction1.setRelatedTransaction(transaction2);

        transactionRepository.save(transaction2);

        return "You send " + transferMoneyDto.getSum() + " to: " + toAccount.getAccountNumber();
    }

    @Override
    @Transactional
    public String withdrawMoney(TransactionDto transactionDto, Authentication authentication) {
        Person person = personService.findByEmail(authentication.getName());
        Account account = getAccountById(transactionDto.getAccountId());

        if (!person.getCustomer().getAccounts().contains(account))
            throw new AccountViolationException("You can't withdraw money from this account");

        account.withdraw(transactionDto.getSum());

        Transaction transaction =  new Transaction(transactionDto.getSum(), account, TransactionType.WITHDRAWAL, null);
        transactionRepository.save(transaction);

        return "You withdrew " + transactionDto.getSum() + " from account: " + account.getAccountNumber();
    }

    @Override
    @Transactional
    public String depositMoney(TransactionDto transactionDto) {
        Account account = getAccountById(transactionDto.getAccountId());

        account.deposit(transactionDto.getSum());

        Transaction transaction = new Transaction(transactionDto.getSum(), account, TransactionType.DEPOSIT, null);
        transactionRepository.save(transaction);

        return "You transferred " + transactionDto.getSum() + " to account: " + account.getAccountNumber();
    }

    private String generateAccountNumber() {
        String num = "";
        while(accountRepository.findByAccountNumber(num).isPresent() || num.length() != 12)
            num = generateRandomAccountNumber();
        return num;
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 11; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
