package ru.arman.bankingsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @Column(name = "person_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @OneToOne
    @MapsId
    @JoinColumn(name = "person_id")
    @JsonIgnoreProperties({"customer"})
    private Person person;

    @ManyToMany (cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "customer_accounts",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    @JsonIgnoreProperties({"customers"})
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account){
        this.accounts.add(account);
        account.getCustomers().add(this);
    }
    public void removeAccount(Account account){
        this.accounts.remove(account);
        account.getCustomers().remove(this);
    }

    public void setPerson(Person person) {
        this.person = person;
        person.setCustomer(this);
    }
}
