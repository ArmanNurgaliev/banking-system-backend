package ru.arman.bankingsystembackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.arman.bankingsystembackend.entity.Loan;
import ru.arman.bankingsystembackend.entity.LoanPayment;

import java.sql.Date;
import java.util.List;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {
    @Query("select p from LoanPayment p where year(p.scheduledPaymentDate) = :year and month(p.scheduledPaymentDate) = :month and  p.paidAmount < p.paymentAmount")
    List<LoanPayment> findAllByScheduledPaymentDate(int year, int month);

    List<LoanPayment> findByLoan(Loan loan);
}