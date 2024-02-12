package ru.arman.bankingsystembackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.arman.bankingsystembackend.entity.Customer;
import ru.arman.bankingsystembackend.entity.Loan;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByCustomer(Customer customer);
}