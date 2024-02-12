package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.dto.AuthenticationRequest;
import ru.arman.bankingsystembackend.dto.AuthenticationResponse;
import ru.arman.bankingsystembackend.dto.RegistrationDto;
import ru.arman.bankingsystembackend.entity.Person;
import ru.arman.bankingsystembackend.entity.PersonAddress;
import ru.arman.bankingsystembackend.entity.Role;
import ru.arman.bankingsystembackend.entity.SecuredPerson;
import ru.arman.bankingsystembackend.repository.PersonRepository;
import ru.arman.bankingsystembackend.service.AuthService;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public String register(RegistrationDto registrationDto) {
        PersonAddress address = PersonAddress.builder()
                .city(registrationDto.getCity())
                .street(registrationDto.getStreet())
                .house(registrationDto.getHouse())
                .apt(registrationDto.getApt())
                .build();

        Person person = Person.builder()
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .role(Role.USER)
                .phoneNumber(registrationDto.getPhoneNumber())
                .dob(registrationDto.getDob())
                .build();

        person.setAddress(address);

        personRepository.save(person);

        return "Person " + person.getFirstName() + " registered.";
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        SecuredPerson person = (SecuredPerson) userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtService.generateAccessToken(person);
        String refreshToken = jwtService.generateRefreshToken(person);

        return AuthenticationResponse.builder()
                .email(person.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
