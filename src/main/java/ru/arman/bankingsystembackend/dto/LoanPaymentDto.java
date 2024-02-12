package ru.arman.bankingsystembackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanPaymentDto {
    @NotNull(message = "Payment id can't be null")
    private Long paymentId;
    @NotNull(message = "Account id can't be null")
    private Long accountId;
    @NotNull(message = "Sum can't be null")
    private BigDecimal sum;
}
