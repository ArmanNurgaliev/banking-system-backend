package ru.arman.bankingsystembackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.arman.bankingsystembackend.dto.PersonDto;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.service.PersonService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService personService;

    @GetMapping("/all")
    public ResponseEntity<Page<Person>> getAllUsers(
            @RequestParam("page") Integer page,
            @RequestParam("rowPerPage") Integer rows,
            @RequestParam("sort") String sort) {
        return ResponseEntity.ok(personService.getAllUsers(page, rows, sort));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Person> updatePerson(@RequestBody PersonDto personDto,
                                               @PathVariable Long id,
                                               Authentication authentication) {
        return ResponseEntity.ok(personService.updatePerson(personDto, id, authentication));
    }

    @PostMapping("/update/role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Person> changeRole(@RequestParam String role, @PathVariable Long id) {
        return ResponseEntity.ok(personService.changeRole(role, id));
    }
}