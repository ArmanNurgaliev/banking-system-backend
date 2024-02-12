package ru.arman.bankingsystembackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanDto {
    @NotBlank(message = "Loan type can't be empty")
    private String loanType;

    @NotNull(message = "Loan amount can't be null")
    @Min(100_000)
    @Max(10_000_000)
    private BigDecimal loanAmount;

    @NotNull(message = "Loan duration can't be null")
    private int term;

    @NotNull(message = "Start date can't be null")
    private Date startDate;
    @NotNull(message = "Branch id can't be null")
    private Long branchId;
    @NotNull(message = "Customer id can't be null")
    private Long customerId;
}
