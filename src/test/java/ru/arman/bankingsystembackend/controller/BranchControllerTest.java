package ru.arman.bankingsystembackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.BranchDto;
import ru.arman.bankingsystembackend.entity.Branch;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.Role;
import ru.arman.bankingsystembackend.entity.SecuredPerson;
import ru.arman.bankingsystembackend.repository.BranchRepository;
import ru.arman.bankingsystembackend.service.impl.JwtService;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@Transactional
class BranchControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private JwtService jwtService;

    private Branch branch;
    private String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void initData() {
        branch = Branch.builder()
                .id(1L)
                .bic("1234567")
                .balance(BigDecimal.valueOf(100000000))
                .name("Main")
                .phoneNumber("45684612")
                .build();
        branch = branchRepository.save(branch);

        Person person = Person.builder()
                .email("some@mail.ru")
                .password("pass")
                .firstName("name")
                .lastName("surname")
                .phoneNumber("12345789")
                .role(Role.ADMIN)
                .build();

        token = "Bearer " + jwtService.generateAccessToken(new SecuredPerson(person));
    }

    @Test
    void getBranchByIdTest_shouldReturnBranch() throws Exception {
        mockMvc.perform(get("/api/branch/" + branch.getId())
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bic").value(branch.getBic()));
    }

    @Test
    void getBranchByIdTest_shouldThrowBranchNotFoundException() throws Exception {
        long id = 10L;
        mockMvc.perform(get("/api/branch/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Branch not found with id: " + id));
    }

    @Test
    void createBranchTest_shouldReturnBranch() throws Exception {
        BranchDto branchDto =
                new BranchDto("Local", "78946512", "123456789",
                        "City", "Lenina" ,"5");

        mockMvc.perform(post("/api/branch/create")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(branchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bic").value(branchDto.getBic()));
    }

    @Test
    void createBranchTest_shouldThrowException() throws Exception {
        BranchDto branchDto =
                new BranchDto("", "78946512", "123456789",
                        "City", "Lenina" ,"5");

        mockMvc.perform(post("/api/branch/create")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(branchDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Branch name can't be empty"));
    }
}