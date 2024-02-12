package ru.arman.bankingsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_generator")
    @SequenceGenerator(name = "loan_generator", sequenceName = "loan_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @NotNull(message = "Loan amount can't be empty")
    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    @NotNull(message = "Duration of loan can't be empty")
    private int term;

    @NotNull(message = "Start date of loan can't be empty")
    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

//    @OneToMany(fetch = FetchType.LAZY)
//    private LoanPayment loanPayment;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
