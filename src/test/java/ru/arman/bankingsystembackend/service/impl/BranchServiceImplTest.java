package ru.arman.bankingsystembackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.arman.bankingsystembackend.TestContainerConfiguration;
import ru.arman.bankingsystembackend.dto.BranchDto;
import ru.arman.bankingsystembackend.entity.Branch;
import ru.arman.bankingsystembackend.entity.BranchAddress;
import ru.arman.bankingsystembackend.exception.BranchAddressAlreadyExistsException;
import ru.arman.bankingsystembackend.repository.BranchRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(TestContainerConfiguration.class)
class BranchServiceImplTest {
    @InjectMocks
    private BranchServiceImpl branchService;

    @Mock
    private BranchRepository branchRepository;

    private BranchDto branchDto;
    private Branch branch;
    private BranchAddress branchAddress;

    @BeforeEach
    void setUp() {
        branchDto = new BranchDto();
        branchDto.setName("Name");
        branchDto.setPhoneNumber("123456789");
        branchDto.setCity("City");
        branchDto.setStreet("Street");
        branchDto.setHouse("5");

        branchAddress = BranchAddress.builder()
                .city(branchDto.getCity())
                .street(branchDto.getStreet())
                .house(branchDto.getHouse())
                .build();

        branch = Branch.builder()
                .id(1L)
                .bic(branchDto.getBic())
                .name(branchDto.getName())
                .phoneNumber(branchDto.getPhoneNumber())
                .address(branchAddress)
                .build();
    }

    @Test
    void createBranchTest_shouldReturnBranch() {
        when(branchRepository.findByAddress(any())).thenReturn(Optional.empty());
        when(branchRepository.save(any())).thenReturn(branch);

        Branch responseBranch = branchService.createBranch(branchDto);

        assertNotNull(responseBranch);
        assertEquals(branchDto.getName(), responseBranch.getName());
    }

    @Test
    void createBranchTest_shouldThrowException() {
        when(branchRepository.findByAddress(any())).thenReturn(Optional.of(branch));

        BranchAddressAlreadyExistsException exception =
                assertThrows(BranchAddressAlreadyExistsException.class,
                        () -> branchService.createBranch(branchDto));

        assertEquals("Branch with this address already exist", exception.getMessage());
    }
}