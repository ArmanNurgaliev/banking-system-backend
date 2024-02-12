package ru.arman.bankingsystembackend.service;

import org.springframework.security.core.Authentication;
import ru.arman.bankingsystembackend.dto.LoanPaymentDto;
import ru.arman.bankingsystembackend.entity.LoanPayment;

import java.util.List;

public interface LoanPaymentService {
    void notifyAboutLoanPayment();
    List<LoanPayment> getPayments(Long loanId, Authentication authentication);

    String payLoanPayment(LoanPaymentDto loanPaymentDto);
}
