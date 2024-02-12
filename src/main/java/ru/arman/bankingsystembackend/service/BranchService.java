package ru.arman.bankingsystembackend.service;

import ru.arman.bankingsystembackend.dto.BranchDto;
import ru.arman.bankingsystembackend.entity.Branch;

public interface BranchService {
    Branch createBranch(BranchDto branchDto);

    Branch getBranchById(Long id);
}
