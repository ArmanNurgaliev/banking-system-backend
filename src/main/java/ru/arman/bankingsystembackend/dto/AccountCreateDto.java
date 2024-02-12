package ru.arman.bankingsystembackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDto {
    @NotBlank(message = "Account type can't be empty")
    private String accountType;
    @NotNull(message = "Branch id can't be null")
    private Long branchId;
}
