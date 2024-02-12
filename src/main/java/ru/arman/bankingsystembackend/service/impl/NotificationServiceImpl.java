package ru.arman.bankingsystembackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.arman.bankingsystembackend.entity.Customer;
import ru.arman.bankingsystembackend.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(Customer customer, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("armanurgaliev@gmail.com");
        msg.setTo(customer.getPerson().getEmail());
        msg.setSubject(subject);
        msg.setText(message);
        try {
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
