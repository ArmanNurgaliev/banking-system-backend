package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.entity.SecuredPerson;
import ru.arman.bankingsystembackend.exception.PersonNotFoundException;
import ru.arman.bankingsystembackend.repository.PersonRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByEmailOrPhoneNumber(username, username)
                .map(SecuredPerson::new)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with email or phone number: " + username));
    }
}
