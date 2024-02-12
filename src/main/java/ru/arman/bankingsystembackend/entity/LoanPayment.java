package ru.arman.bankingsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class LoanPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_payment_generator")
    @SequenceGenerator(name = "loan_payment_generator", sequenceName = "loan_payment_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private Date scheduledPaymentDate;

//  The expected total amount to be paid on the scheduled date
    private BigDecimal paymentAmount;
    //The expected principal amount to be paid on the scheduled date.
    private BigDecimal principalAmount;
    //The expected interest amount to be paid on the scheduled date.
    private BigDecimal interestAmount;

    private BigDecimal residualAmount;
//    private BigDecimal toBePaid;

    //The actual amount paid
    private BigDecimal paidAmount;

    private Date paidDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"customer"})
    private Loan loan;
}
