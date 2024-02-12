package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.dto.AccountCreateDto;
import ru.arman.bankingsystembackend.dto.LoanDto;
import ru.arman.bankingsystembackend.dto.LoanInfoDto;
import ru.arman.bankingsystembackend.entity.*;
import ru.arman.bankingsystembackend.exception.LoanNotFoundException;
import ru.arman.bankingsystembackend.repository.CustomerRepository;
import ru.arman.bankingsystembackend.repository.LoanPaymentRepository;
import ru.arman.bankingsystembackend.repository.LoanRepository;
import ru.arman.bankingsystembackend.repository.TransactionRepository;
import ru.arman.bankingsystembackend.service.AccountService;
import ru.arman.bankingsystembackend.service.LoanService;
import ru.arman.bankingsystembackend.service.PersonService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final AccountService accountService;
    private final PersonService personService;
    private final TransactionRepository transactionRepository;

    @Override
    public List<LoanPayment> applyForACredit(LoanDto loanDto, Authentication authentication)  {
        Customer customer = customerRepository.getReferenceById(loanDto.getCustomerId());

        Loan loan = Loan.builder()
                .loanType(LoanType.valueOf(loanDto.getLoanType().toUpperCase()))
                .loanAmount(loanDto.getLoanAmount())
                .customer(customer)
                .startDate(loanDto.getStartDate())
                .endDate(Date.valueOf(loanDto.getStartDate().toLocalDate().plusMonths(loanDto.getTerm())))
                .term(loanDto.getTerm())
                .status(Status.ACTIVE)
                .build();

        if (loan.getLoanType().equals(LoanType.MORTGAGE))
            loan.setInterestRate(BigDecimal.valueOf(671, 2));
        else if (loan.getLoanType().equals(LoanType.PERSONAL))
            loan.setInterestRate(BigDecimal.valueOf(1000, 2));
        else if (loan.getLoanType().equals(LoanType.BUSINESS))
            loan.setInterestRate(BigDecimal.valueOf(900, 2));

        Account account;
        if (customer.getAccounts().isEmpty())
            account = accountService.createAccount(new AccountCreateDto(AccountType.CHECKING.name(), loanDto.getBranchId()), authentication);
        else
            account = customer.getAccounts().get(0);

        Branch branch = account.getBranch();
        account.deposit(loan.getLoanAmount());
        branch.withdraw(loan.getLoanAmount());

        Transaction transaction = new Transaction(loan.getLoanAmount(), account, TransactionType.LOAN, null);
        transactionRepository.save(transaction);

        return calculateLoanPayment(loan);
    }

    private List<LoanPayment> calculateLoanPayment(Loan loan) {
        BigDecimal rate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(100), 5, RoundingMode.CEILING);

        double monthRate = rate.divide(BigDecimal.valueOf(12), 5, RoundingMode.CEILING).doubleValue();

        double k = monthRate * Math.pow((1 + monthRate), loan.getTerm()) / (Math.pow((1 + monthRate), loan.getTerm()) - 1);

        BigDecimal residualAmount = loan.getLoanAmount();
        BigDecimal paymentAmount = residualAmount.multiply(BigDecimal.valueOf(k));

        List<LoanPayment> payments = new ArrayList<>();

        for (int i = 0; i < loan.getTerm(); i++) {
            BigDecimal interestAmount = residualAmount.multiply(rate)
                    .multiply(BigDecimal.valueOf(loan.getStartDate().toLocalDate().plusMonths(i).lengthOfMonth()))
                    .divide(BigDecimal.valueOf(loan.getStartDate().toLocalDate().plusMonths(i).lengthOfYear()),5, RoundingMode.CEILING);
            BigDecimal principalAmount;

            if (i == loan.getTerm()-1) {
                principalAmount = residualAmount;
                residualAmount = BigDecimal.ZERO;
                paymentAmount = principalAmount.add(interestAmount);
            } else {
                principalAmount = paymentAmount.subtract(interestAmount);
                residualAmount = residualAmount.subtract(principalAmount);
            }

            LoanPayment loanPayment = LoanPayment.builder()
                    .scheduledPaymentDate(Date.valueOf(loan.getStartDate().toLocalDate().plusMonths(i+1)))
                    .paymentAmount(paymentAmount.setScale(2, RoundingMode.CEILING))
                    .principalAmount(principalAmount.setScale(2, RoundingMode.CEILING))
                    .interestAmount(interestAmount.setScale(2, RoundingMode.CEILING))
                    .residualAmount(residualAmount.setScale(2, RoundingMode.CEILING))
                    .paidAmount(BigDecimal.ZERO)
//                    .toBePaid(paymentAmount.setScale(2, RoundingMode.CEILING))
                    .loan(loan)
                    .build();

            payments.add(loanPaymentRepository.save(loanPayment));
        }

        return payments;
    }

    @Override
    public List<LoanInfoDto> getAllLoans(String status, Authentication authentication) {
        Person person = personService.findByEmail(authentication.getName());
        List<Loan> loans = loanRepository.findAllByCustomer(person.getCustomer());

        if (status != null) {
            loans = loans.stream()
                    .filter(l -> l.getStatus().equals(Status.valueOf(status.toUpperCase())))
                    .toList();
        }

        List<LoanInfoDto> loanInfo = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        for(Loan loan: loans) {
            List<LoanPayment> payments = loanPaymentRepository.findByLoan(loan).stream()
                    .sorted(Comparator.comparing(LoanPayment::getScheduledPaymentDate))
                    .filter(lp -> lp.getScheduledPaymentDate().before(cal.getTime()) && lp.getPaidDate() == null)
                    .toList();

            LoanInfoDto loanInfoDto = LoanInfoDto.builder()
                    .loanType(loan.getLoanType())
                    .loanAmount(loan.getLoanAmount())
                    .interestRate(loan.getInterestRate())
                    .term(loan.getTerm())
                    .startDate(loan.getStartDate())
                    .endDate(loan.getEndDate())
                    .status(loan.getStatus())
                    .toBePaid(payments.stream().map(LoanPayment::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add))
                    .unpaidMonths(payments.size())
                    .build();
            loanInfo.add(loanInfoDto);
        }

        return loanInfo;
    }

    @Override
    public Loan getById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("LOan not found with id: " + id));
    }
}
