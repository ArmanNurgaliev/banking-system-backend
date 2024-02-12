package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Customer;
import ru.arman.bankingsystembackend.entity.CustomerType;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.Role;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findAllByCustomerTypeTest_shouldReturnCustomers() {
        Person person1 = Person.builder()
                .email("some@mail.ru")
                .password("pass")
                .firstName("name")
                .lastName("surname")
                .phoneNumber("12345789")
                .role(Role.CUSTOMER)
                .build();
        Customer customer1 = new Customer();
        customer1.setCustomerType(CustomerType.PREMIUM);
        customer1.setPerson(person1);

        Person person2 = Person.builder()
                .email("some1@mail.ru")
                .password("pass")
                .firstName("nam1")
                .lastName("surname1")
                .phoneNumber("123452389")
                .role(Role.CUSTOMER)
                .build();
        Customer customer2 = new Customer();
        customer2.setCustomerType(CustomerType.PREMIUM);
        customer2.setPerson(person2);

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findAllByCustomerType(CustomerType.PREMIUM);

        assertThat(customers).hasSize(2).contains(customer1, customer2);
    }

    @Test
    void findAllByCustomerTypeTest_shouldBeEmpty() {
        List<Customer> customers = customerRepository.findAllByCustomerType(CustomerType.PREMIUM);

        assertThat(customers).isEmpty();
    }
}