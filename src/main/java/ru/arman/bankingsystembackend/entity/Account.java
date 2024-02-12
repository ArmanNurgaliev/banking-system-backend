package ru.arman.bankingsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import ru.arman.bankingsystembackend.exception.NotEnoughMoneyException;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
    @SequenceGenerator(name = "account_generator", sequenceName = "account_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(length = 20)
    private String accountNumber;

    private BigDecimal currentBalance;

    private Date dateOpened;
    private Date dateClosed;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @ManyToOne
    private Branch branch;

    @ManyToMany(mappedBy = "accounts")
    @JsonIgnoreProperties({"accounts"})
    private Set<Customer> customers = new HashSet<>();

    public synchronized void deposit(BigDecimal sum)  {
        setCurrentBalance(getCurrentBalance().add(sum));
    }

    public synchronized void withdraw(BigDecimal sum)  {
        if (getCurrentBalance().compareTo(sum) < 0)
            throw new NotEnoughMoneyException("You don't have enough money");
        setCurrentBalance(getCurrentBalance().subtract(sum));
    }
}
