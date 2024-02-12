package ru.arman.bankingsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_generator")
    @SequenceGenerator(name = "person_generator", sequenceName = "person_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    @JsonIgnore
    private String password;

    @NotBlank(message = "FirstName can't be empty")
    private String firstName;

    @NotBlank(message = "LastName can't be empty")
    private String lastName;

    private Date dob;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email can't be blank")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 20)
    @NotBlank(message = "Phone can't be blank")
    private String phoneNumber;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private PersonAddress address;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"person"})
    private Customer customer;

    public void setAddress(PersonAddress address) {
        this.address = address;
        this.address.setPerson(this);
    }

//    public void setCustomer(Customer customer) {
//        this.customer = customer;
//        this.customer.setPerson(this);
//    }
}
