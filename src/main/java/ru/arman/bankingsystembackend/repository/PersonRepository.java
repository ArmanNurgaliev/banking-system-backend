package ru.arman.bankingsystembackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.arman.bankingsystembackend.entity.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmailOrPhoneNumber(String email, String phoneNumber);

    Optional<Person> findByEmail(String name);
}