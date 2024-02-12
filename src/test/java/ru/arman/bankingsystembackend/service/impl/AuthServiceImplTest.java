package ru.arman.bankingsystembackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.AuthenticationRequest;
import ru.arman.bankingsystembackend.dto.AuthenticationResponse;
import ru.arman.bankingsystembackend.dto.RegistrationDto;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.Role;
import ru.arman.bankingsystembackend.entity.SecuredPerson;
import ru.arman.bankingsystembackend.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestContainerConfiguration.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtService jwtService;

    private Person person;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .email("some@mail.ru")
                .password("pass")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    void registerTest_shouldReturnMessage() {
        RegistrationDto registrationDto = RegistrationDto.builder()
                .email("some@mail.ru")
                .password("pass")
                .phoneNumber("123456789")
                .firstName("name")
                .lastName("surname")
                .city("Tomsk")
                .street("Lenina")
                .house("2")
                .build();

        String response = authService.register(registrationDto);

        assertEquals("Person " + registrationDto.getFirstName() + " registered.", response);
    }

    @Test
    void loginTest_shouldReturnAuthResponse() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("some@mail.ru");
        authenticationRequest.setPassword("pass");
        SecuredPerson securedPerson = new SecuredPerson(person);

        when(userDetailsService.loadUserByUsername(any())).thenReturn(securedPerson);

        AuthenticationResponse response = authService.login(authenticationRequest);

        assertEquals(authenticationRequest.getEmail(), response.getEmail());
    }
}