package ru.arman.bankingsystembackend.dto;

import lombok.*;
import ru.arman.bankingsystembackend.entity.LoanType;
import ru.arman.bankingsystembackend.entity.Status;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoanInfoDto {
    private LoanType loanType;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private int term;

    private Date startDate;
    private Date endDate;

    private Status status;

    private BigDecimal toBePaid;
    private int unpaidMonths;
}
