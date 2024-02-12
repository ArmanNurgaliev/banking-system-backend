package ru.arman.bankingsystembackend.exception;

public class LoanPaymentNotFoundException extends RuntimeException {
    public LoanPaymentNotFoundException(String message) {
        super(message);
    }
}
