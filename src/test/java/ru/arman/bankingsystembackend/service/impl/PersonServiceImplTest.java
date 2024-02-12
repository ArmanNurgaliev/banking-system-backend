package ru.arman.bankingsystembackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.Role;
import ru.arman.bankingsystembackend.exception.PersonNotFoundException;
import ru.arman.bankingsystembackend.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestContainerConfiguration.class)
class PersonServiceImplTest {
    @InjectMocks
    private PersonServiceImpl personService;
    @Mock
    private PersonRepository personRepository;

    private Person person;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1L)
                .role(Role.USER)
                .password("pass")
                .email("email@mail.ru")
                .build();
    }

    @Test
    void findByEmailTest_shouldReturnPerson() {
        when(personRepository.findByEmail(any())).thenReturn(Optional.of(person));

        Person byEmail = personService.findByEmail(person.getEmail());

        assertEquals(person, byEmail);
    }

    @Test
    void findByEmailTest_shouldThrowException() {
        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class,
                () -> personService.findByEmail(person.getEmail()));

        assertEquals("Person not found with email: " + person.getEmail(), exception.getMessage());
    }
}