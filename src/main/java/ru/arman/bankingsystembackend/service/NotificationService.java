package ru.arman.bankingsystembackend.service;

import ru.arman.bankingsystembackend.entity.Customer;

public interface NotificationService {
    void sendEmail(Customer customer, String subject, String message);
}
