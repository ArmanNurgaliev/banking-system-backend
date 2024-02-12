package ru.arman.bankingsystembackend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.dto.PersonDto;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.PersonAddress;
import ru.arman.bankingsystembackend.entity.Role;
import ru.arman.bankingsystembackend.exception.PersonNotFoundException;
import ru.arman.bankingsystembackend.repository.PersonRepository;
import ru.arman.bankingsystembackend.service.PersonService;

@AllArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public Person findByEmail(String email) {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with email: " + email));
    }

    private Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
    }

    @Override
    public Person updatePerson(PersonDto personDto, Long id, Authentication authentication) {
        Person changingPerson = findByEmail(authentication.getName());
        Person personFromDb = findById(id);

        if (!personFromDb.getEmail().equals(authentication.getName()) &&
                !changingPerson.getRole().equals(Role.MANAGER) &&
                !changingPerson.getRole().equals(Role.ADMIN))
            throw new AccessDeniedException("You can't change person's data");

        PersonAddress address = personFromDb.getAddress();

        if (personDto.getFirstName() != null && !personDto.getFirstName().isEmpty())
            personFromDb.setFirstName(personDto.getFirstName());
        if (personDto.getLastName() != null && !personDto.getLastName().isEmpty())
            personFromDb.setLastName(personDto.getLastName());
        if (personDto.getDob() != null)
            personFromDb.setDob(personDto.getDob());
        if (personDto.getEmail() != null && !personDto.getEmail().isEmpty())
            personFromDb.setEmail(personDto.getEmail());
        if (personDto.getPhoneNumber() != null && !personDto.getPhoneNumber().isEmpty())
            personFromDb.setPhoneNumber(personDto.getPhoneNumber());
        if (personDto.getCity() != null && !personDto.getCity().isEmpty())
            address.setCity(personDto.getCity());
        if (personDto.getStreet() != null && !personDto.getStreet().isEmpty())
            address.setStreet(personDto.getStreet());
        if (personDto.getHouse() != null && !personDto.getHouse().isEmpty())
            address.setHouse(personDto.getHouse());
        if (personDto.getApt() != null && !personDto.getApt().isEmpty())
            address.setApt(personDto.getApt());

        personFromDb.setAddress(address);

        return personRepository.save(personFromDb);
    }

    @Override
    public Person changeRole(String role, Long id) {
        Person personFromDb = findById(id);

        if (role != null && !role.isEmpty())
            personFromDb.setRole(Role.valueOf(role.toUpperCase()));

        return personRepository.save(personFromDb);
    }

    @Override
    public Page<Person> getAllUsers(Integer page, Integer rows, String sort) {
        return personRepository.findAll(PageRequest.of(page, rows, Sort.by(sort)));
    }
}