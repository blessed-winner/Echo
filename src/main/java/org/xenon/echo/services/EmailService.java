package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    public void sendVerificationEmail(String to, String token) {
        // TODO: Implement actual email sending logic (e.g., using JavaMailSender)
        System.out.println("Sending verification email to: " + to);
        System.out.println("Verification token: " + token);
    }

    public void sendPasswordResetEmail(String to, String token) {
        // TODO: Implement actual email sending logic
        System.out.println("Sending password reset email to: " + to);
        System.out.println("Reset token: " + token);
    }

}
