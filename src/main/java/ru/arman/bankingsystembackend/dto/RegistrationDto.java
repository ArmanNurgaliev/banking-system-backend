package ru.arman.bankingsystembackend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    @NotBlank(message = "Password can't be empty")
    private String password;

    @NotBlank(message = "FirstName can't be empty")
    private String firstName;

    @NotBlank(message = "LastName can't be empty")
    private String lastName;

    private Date dob;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email can't be blank")
    private String email;

    @Column(length = 20)
    @NotBlank(message = "Phone can't be blank")
    private String phoneNumber;

    @NotBlank(message = "City can't be empty")
    private String city;
    @NotBlank(message = "Street can't be empty")
    private String street;
    @NotBlank(message = "House can't be empty")
    private String house;
    private String apt;
}
