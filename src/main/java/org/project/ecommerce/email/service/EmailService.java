package org.project.ecommerce.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your email");
        message.setText("Click here to verify: " + link);
        mailSender.send(message);
    }
}
