package ru.arman.bankingsystembackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.entity.Branch;
import ru.arman.bankingsystembackend.entity.BranchAddress;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BranchRepositoryTest {
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByAddressTest_shouldReturnBranch() {
        BranchAddress address = BranchAddress.builder()
                .city("City")
                .street("Street")
                .house("1")
                .build();
        Branch branch = Branch.builder()
                .id(1L)
                .bic("1234567")
                .balance(BigDecimal.valueOf(100000000))
                .name("Main")
                .phoneNumber("45684612")
                .build();
        branch.setAddress(address);
        branchRepository.save(branch);

        Branch foundBranch = branchRepository.findByAddress(address).get();

        assertEquals(entityManager.find(Branch.class, branch.getId()), foundBranch);
    }
}