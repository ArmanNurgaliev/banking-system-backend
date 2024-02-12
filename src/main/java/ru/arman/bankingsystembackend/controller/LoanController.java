package ru.arman.bankingsystembackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.bankingsystembackend.dto.LoanDto;
import ru.arman.bankingsystembackend.dto.LoanInfoDto;
import ru.arman.bankingsystembackend.entity.LoanPayment;
import ru.arman.bankingsystembackend.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<List<LoanPayment>> applyForALoan(@RequestBody @Valid LoanDto loanDto,
                                                           Authentication authentication) {
        return ResponseEntity.ok(loanService.applyForACredit(loanDto, authentication));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanInfoDto>> getAllLoans(@RequestParam(required = false) String status,
                                                         Authentication authentication) {
        return ResponseEntity.ok(loanService.getAllLoans(status, authentication));
    }
}
