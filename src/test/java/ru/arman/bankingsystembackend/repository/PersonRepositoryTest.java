package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.Role;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Person person;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1L)
                .email("some@mail.ru")
                .password("pass")
                .firstName("name")
                .lastName("surname")
                .phoneNumber("12345789")
                .role(Role.CUSTOMER)
                .build();
        person = personRepository.save(person);
    }

    @Test
    void findByEmailOrPhoneNumberTest_shouldReturnPerson() {
        Optional<Person> foundPerson1 =
                personRepository.findByEmailOrPhoneNumber(person.getEmail(), "7846645");

        Optional<Person> foundPerson2 =
                personRepository.findByEmailOrPhoneNumber("aaaa@mail.ru", person.getPhoneNumber());

        assertThat(foundPerson1.get())
                .isEqualTo(foundPerson2.get())
                .isEqualTo(entityManager.find(Person.class, person.getId()));
    }

    @Test
    void findByEmailTest_shouldReturnPerson() {
        Optional<Person> foundPerson = personRepository.findByEmail(person.getEmail());

        assertThat(foundPerson.get()).isEqualTo(entityManager.find(Person.class, person.getId()));
    }
}