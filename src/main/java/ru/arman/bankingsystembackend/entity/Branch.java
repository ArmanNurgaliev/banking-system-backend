package ru.arman.bankingsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.arman.bankingsystembackend.exception.NotEnoughMoneyException;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_generator")
    @SequenceGenerator(name = "branch_generator", sequenceName = "branch_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotBlank(message = "Branch name can't be empty")
    private String name;

    @NotBlank(message = "Branch code can't be empty")
    @Column(length = 9, unique = true)
    private String bic;

    private BigDecimal balance;

    @Column(length = 20)
    @NotBlank(message = "Phone can't be blank")
    private String phoneNumber;

    @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL)
    private BranchAddress address;

    public void setAddress(BranchAddress address) {
        this.address = address;
        this.address.setBranch(this);
    }

    public synchronized void deposit(BigDecimal sum)  {
        setBalance(getBalance().add(sum));
    }

    public synchronized void withdraw(BigDecimal sum) {
        if (getBalance().compareTo(sum) < 0)
            throw new NotEnoughMoneyException("Branch doesn't have enough money");
        setBalance(getBalance().subtract(sum));
    }
}

