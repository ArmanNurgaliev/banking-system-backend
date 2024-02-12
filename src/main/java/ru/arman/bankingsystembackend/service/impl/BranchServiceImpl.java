package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.dto.BranchDto;
import ru.arman.bankingsystembackend.entity.Branch;
import ru.arman.bankingsystembackend.entity.BranchAddress;
import ru.arman.bankingsystembackend.exception.BranchAddressAlreadyExistsException;
import ru.arman.bankingsystembackend.exception.BranchNotFoundException;
import ru.arman.bankingsystembackend.repository.BranchRepository;
import ru.arman.bankingsystembackend.service.BranchService;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    @Override
    public Branch createBranch(BranchDto branchDto) {
        BranchAddress branchAddress = BranchAddress.builder()
                .city(branchDto.getCity())
                .street(branchDto.getStreet())
                .house(branchDto.getHouse())
                .build();

        if (branchRepository.findByAddress(branchAddress).isPresent())
            throw new BranchAddressAlreadyExistsException("Branch with this address already exist");

        Branch branch = Branch.builder()
                .name(branchDto.getName())
                .bic(branchDto.getBic())
                .phoneNumber(branchDto.getPhoneNumber())
                .build();
        branch.setAddress(branchAddress);

        return branchRepository.save(branch);
    }

    @Override
    public Branch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(()-> new BranchNotFoundException("Branch not found with id: " + id));
    }
}
