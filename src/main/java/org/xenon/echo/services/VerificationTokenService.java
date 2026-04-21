package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.repositories.VerificationTokenRepository;

@Service
@AllArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

}
