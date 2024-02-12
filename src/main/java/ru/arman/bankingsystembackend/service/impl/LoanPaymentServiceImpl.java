package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.dto.LoanPaymentDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.exception.AccountViolationException;
import ru.arman.bankingsystembackend.repository.LoanPaymentRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;
import ru.arman.bankingsystembackend.service.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanPaymentServiceImpl implements LoanPaymentService {
    private final NotificationService notificationService;
    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanService loanService;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final PersonService personService;

    @Override
    @Scheduled(cron = "0 0 9 1 * *")
    public void notifyAboutLoanPayment() {
        List<LoanPayment> payments =
                loanPaymentRepository.findAllByScheduledPaymentDate(LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue());

        payments.forEach(p ->  notificationService.sendEmail(p.getLoan().getCustomer(),
                "Loan payment",
                "Hello, we remind you that in " +
                        "this month you have a loan payment of " + p.getPaymentAmount())
        );
    }

    public List<LoanPayment> getPayments(Long loanId, Authentication authentication) {
        Person person = personService.findByEmail(authentication.getName());
        Loan loan = loanService.getById(loanId);

        if (!loan.getCustomer().getId().equals(person.getId()))
            throw new AccountViolationException("You can't get info about this loan");

        return loanPaymentRepository.findByLoan(loan);
    }

    @Override
    public String payLoanPayment(LoanPaymentDto loanPaymentDto)  {
        Account account = accountService.getAccountById(loanPaymentDto.getAccountId());
        LoanPayment loanPayment = getLoanPayment(loanPaymentDto.getPaymentId());

        boolean fullPayment = loanPaymentDto.getSum()
                .compareTo(loanPayment.getPaymentAmount().subtract(loanPayment.getPaidAmount())) >= 0;

        if (fullPayment)
            loanPaymentDto.setSum(loanPayment.getPaymentAmount().subtract(loanPayment.getPaidAmount()));

        Branch branch = account.getBranch();
        account.withdraw(loanPaymentDto.getSum());
        branch.deposit(loanPaymentDto.getSum());

//        loanPayment.setToBePaid(loanPayment.getToBePaid().subtract(loanPaymentDto.getSum()));
        loanPayment.setPaidAmount(loanPayment.getPaidAmount().add(loanPaymentDto.getSum()));

        if (fullPayment) {
            loanPayment.setPaidDate(new Date(System.currentTimeMillis()));
            if (loanPayment.getResidualAmount().equals(BigDecimal.ZERO))
                loanPayment.getLoan().setStatus(Status.CLOSED);
        }

        Transaction transaction = new Transaction(loanPaymentDto.getSum(), account, TransactionType.LOAN_PAYMENT, loanPayment);

        LoanPayment savedLoanPayment = loanPaymentRepository.save(loanPayment);
        transactionRepository.save(transaction);

        return "Loan payment of " + loanPaymentDto.getSum() +
                " paid with account: " + account.getAccountNumber() + ". Remains to be paid: " +
                (savedLoanPayment.getPaymentAmount().subtract(savedLoanPayment.getPaidAmount()));
    }

    private LoanPayment getLoanPayment(Long paymentId) {
        return loanPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id " + paymentId));
    }

}
