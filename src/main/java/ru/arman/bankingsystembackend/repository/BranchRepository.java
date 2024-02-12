package ru.arman.bankingsystembackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.arman.bankingsystembackend.entity.Branch;
import ru.arman.bankingsystembackend.entity.BranchAddress;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByAddress(BranchAddress address);
}