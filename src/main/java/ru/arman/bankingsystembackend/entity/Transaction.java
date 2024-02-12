package ru.arman.bankingsystembackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_generator")
    @SequenceGenerator(name = "transaction_generator", sequenceName = "transaction_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    private Date transactionDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "related_transaction_id")
    private Transaction relatedTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private LoanPayment loanPayment;

    public Transaction(BigDecimal amount, Account account, TransactionType transactionType, LoanPayment loanPayment) {
        this.amount = amount;
        this.account = account;
        this.transactionType = transactionType;
        this.loanPayment = loanPayment;
        this.transactionDate = new Date(System.currentTimeMillis());
    }

    public void setRelatedTransaction(Transaction transaction) {
        this.relatedTransaction = transaction;
        transaction.relatedTransaction = this;
    }
}
