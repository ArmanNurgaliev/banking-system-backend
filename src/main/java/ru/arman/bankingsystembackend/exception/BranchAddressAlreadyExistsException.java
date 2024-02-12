package ru.arman.bankingsystembackend.exception;

public class BranchAddressAlreadyExistsException extends RuntimeException {
    public BranchAddressAlreadyExistsException(String message) {
        super(message);
    }
}
