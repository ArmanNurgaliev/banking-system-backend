package ru.arman.bankingsystembackend.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import ru.arman.bankingsystembackend.dto.PersonDto;
import ru.arman.bankingsystembackend.entity.Person;

import java.util.List;

public interface PersonService {
    Person findByEmail(String email);

    Person updatePerson(PersonDto personDto, Long id, Authentication authentication);

    Person changeRole(String role, Long id);

    Page<Person> getAllUsers(Integer page, Integer rows, String sort);
}
