package ru.arman.bankingsystembackend.service;

import ru.arman.bankingsystembackend.dto.AuthenticationRequest;
import ru.arman.bankingsystembackend.dto.AuthenticationResponse;
import ru.arman.bankingsystembackend.dto.RegistrationDto;

public interface AuthService {
    public String register(RegistrationDto registrationDto);
    public AuthenticationResponse login(AuthenticationRequest request);
}
