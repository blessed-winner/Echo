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
        String verifyUrl = "https//echo.com/verify?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String message = "Click the link below to verify:\n" + verifyUrl;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Email Verification");
        mail.setText(message);

        mailSender.send(mail);
    }

    public void sendPasswordResetEmail(String to, String token){
        String resetUrl = "https//echo.com/reset-password?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String message = "Click the link below to reset your password:\n" + resetUrl;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Reset Password");
        mail.setText(message);

        mailSender.send(mail);
    }
}
