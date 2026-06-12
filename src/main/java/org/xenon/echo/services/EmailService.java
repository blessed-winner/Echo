package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    
    public void sendVerificationEmail(String to, String token){
        String verifyUrl = "http://localhost:5173/auth/verify?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String message = "Click the link below to verify your email:\n" + verifyUrl;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Verify your Echo account");
        mail.setText(message);

        mailSender.send(mail);
    }

    public void sendPasswordResetEmail(String to, String token){
        String resetUrl = "http://localhost:5173/auth/reset-password?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String message = "Click the link below to reset your password:\n" + resetUrl;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Reset your Echo password");
        mail.setText(message);

        mailSender.send(mail);
    }
}
