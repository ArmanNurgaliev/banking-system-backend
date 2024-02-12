package ru.arman.bankingsystembackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.arman.bankingsystembackend.dto.BranchDto;
import ru.arman.bankingsystembackend.entity.Branch;
import ru.arman.bankingsystembackend.service.BranchService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/branch")
public class BranchController {
    private final BranchService branchService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Branch> createBranch(@RequestBody @Valid BranchDto branchDto) {
        return ResponseEntity.ok(branchService.createBranch(branchDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }
}
