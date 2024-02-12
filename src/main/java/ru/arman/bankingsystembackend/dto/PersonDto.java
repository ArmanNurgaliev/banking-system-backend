package ru.arman.bankingsystembackend.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
    private String firstName;
    private String lastName;

    private Date dob;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String city;
    private String street;
    private String house;
    private String apt;
}
