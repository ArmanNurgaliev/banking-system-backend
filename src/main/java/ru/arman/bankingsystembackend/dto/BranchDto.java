package ru.arman.bankingsystembackend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchDto {
    @NotBlank(message = "Branch name can't be empty")
    private String name;

    @NotBlank(message = "Branch code can't be empty")
    @Column(length = 9, unique = true)
    private String bic;

    @Column(length = 20)
    @NotBlank(message = "Phone can't be blank")
    private String phoneNumber;

    @NotBlank(message = "City can't be empty")
    private String city;
    @NotBlank(message = "Street can't be empty")
    private String street;
    @NotBlank(message = "House can't be empty")
    private String house;
}
