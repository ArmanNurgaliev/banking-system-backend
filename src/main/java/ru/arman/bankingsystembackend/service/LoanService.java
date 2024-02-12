package ru.arman.bankingsystembackend.service;

import org.springframework.security.core.Authentication;
import ru.arman.bankingsystembackend.dto.LoanDto;
import ru.arman.bankingsystembackend.dto.LoanInfoDto;
import ru.arman.bankingsystembackend.entity.Loan;
import ru.arman.bankingsystembackend.entity.LoanPayment;

import java.util.List;

public interface LoanService {
    List<LoanPayment> applyForACredit(LoanDto loanDto, Authentication authentication);
    List<LoanInfoDto> getAllLoans(String status, Authentication authentication);
    Loan getById(Long id);
}
