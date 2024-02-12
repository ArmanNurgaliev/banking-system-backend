package ru.arman.bankingsystembackend.service;

import org.springframework.security.core.Authentication;
import ru.arman.bankingsystembackend.dto.AccountCreateDto;
import ru.arman.bankingsystembackend.dto.TransactionDto;
import ru.arman.bankingsystembackend.dto.TransferMoneyDto;
import ru.arman.bankingsystembackend.entity.Account;

import java.util.List;

public interface AccountService {
    Account getAccountById(Long accountId);
    Account createAccount(AccountCreateDto accountCreateDto, Authentication authentication);

    List<Account> getAllAccount(Authentication authentication);

    String transferMoney(TransferMoneyDto transferMoneyDto, Authentication authentication);

    String withdrawMoney(TransactionDto transactionDto, Authentication authentication);

    String depositMoney(TransactionDto transactionDto);
}
