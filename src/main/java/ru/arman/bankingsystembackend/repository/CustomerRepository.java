package ru.arman.bankingsystembackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.arman.bankingsystembackend.entity.Customer;
import ru.arman.bankingsystembackend.entity.CustomerType;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByCustomerType(CustomerType customerType);
}