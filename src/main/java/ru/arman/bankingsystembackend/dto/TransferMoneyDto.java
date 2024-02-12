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
public class TransferMoneyDto {
    @NotNull(message = "Account id can't be null")
    private Long fromId;
    @NotNull(message = "Account destination can't be null")
    private Long toId;
    @NotNull(message = "Sum can't be null")
    private BigDecimal sum;
}
