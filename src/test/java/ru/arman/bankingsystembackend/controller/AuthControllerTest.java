package ru.arman.bankingsystembackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.AuthenticationRequest;
import ru.arman.bankingsystembackend.dto.RegistrationDto;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.Role;
import ru.arman.bankingsystembackend.entity.SecuredPerson;
import ru.arman.bankingsystembackend.repository.PersonRepository;
import ru.arman.bankingsystembackend.service.impl.JwtService;

import java.sql.Date;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@Transactional
class AuthControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    private Person person;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void initData() {
        person = Person.builder()
                .email("some@mail.ru")
                .password("$2a$10$B8r2eHXFkL4xP18MDtqtNeFRoNgvZtBu94VEYuq5RJ09ds37CmyQS")
                .firstName("name")
                .lastName("surname")
                .phoneNumber("12345789")
                .role(Role.CUSTOMER)
                .build();
        personRepository.save(person);

        objectMapper = new ObjectMapper();
    }

    @Test
    void registerTest_shouldReturnString() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(
                "pass", "firstname", "lastname", new Date(System.currentTimeMillis()),
                "email@mail.ru", "7894512348", "city", "street", "5", "12"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Person " + registrationDto.getFirstName() + " registered."));
    }

    @Test
    void loginTest_shouldReturnAuthResponse() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest(person.getEmail(), "pass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(authRequest.getEmail()))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void loginTest_shouldThrowException() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest(person.getEmail(), "password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}