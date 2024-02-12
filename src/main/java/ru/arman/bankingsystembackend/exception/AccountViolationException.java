package ru.arman.bankingsystembackend.exception;

public class AccountViolationException extends RuntimeException {
    public AccountViolationException(String message) {
        super(message);
    }
}
