package ru.arman.bankingsystembackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.bankingsystembackend.dto.LoanPaymentDto;
import ru.arman.bankingsystembackend.entity.LoanPayment;
import ru.arman.bankingsystembackend.service.LoanPaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class LoanPaymentController {
    private final LoanPaymentService loanPaymentService;

    @GetMapping("/{loanId}")
    public ResponseEntity<List<LoanPayment>> getAllLoanPayments(@PathVariable Long loanId,
                                                                Authentication authentication) {
        return ResponseEntity.ok(loanPaymentService.getPayments(loanId, authentication));
    }

    @PostMapping("/pay")
    public ResponseEntity<String> payLoanPayment(@RequestBody @Valid LoanPaymentDto loanPaymentDto) {
        return ResponseEntity.ok(loanPaymentService.payLoanPayment(loanPaymentDto));
    }
}
