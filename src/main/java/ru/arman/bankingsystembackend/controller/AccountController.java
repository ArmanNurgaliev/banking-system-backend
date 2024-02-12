package ru.arman.bankingsystembackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.bankingsystembackend.dto.AccountCreateDto;
import ru.arman.bankingsystembackend.dto.TransactionDto;
import ru.arman.bankingsystembackend.dto.TransferMoneyDto;
import ru.arman.bankingsystembackend.entity.Account;
import ru.arman.bankingsystembackend.service.AccountService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody @Valid AccountCreateDto accountCreateDto,
                                                 Authentication authentication) {
        return ResponseEntity.ok(accountService.createAccount(accountCreateDto, authentication));
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAll(Authentication authentication) {
        return ResponseEntity.ok(accountService.getAllAccount(authentication));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody @Valid TransferMoneyDto transferMoneyDto,
                                                Authentication authentication) {
        return ResponseEntity.ok(accountService.transferMoney(transferMoneyDto, authentication));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(@RequestBody @Valid TransactionDto transactionDto,
                                                Authentication authentication) {
        return ResponseEntity.ok(accountService.withdrawMoney(transactionDto, authentication));
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositMoney(@RequestBody @Valid TransactionDto transactionDto)  {
        return ResponseEntity.ok(accountService.depositMoney(transactionDto));
    }
}
