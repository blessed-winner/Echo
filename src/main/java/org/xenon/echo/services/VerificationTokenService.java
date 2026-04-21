package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.entities.VerificationToken;
import org.xenon.echo.enums.TokenType;
import org.xenon.echo.repositories.VerificationTokenRepository;
import org.xenon.echo.utils.TokenUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final TokenUtil tokenUtil;
    public String createToken(UUID userId, TokenType tokenType, Duration ttl){
        verificationTokenRepository.deleteByUserIdAndType(userId, tokenType);

       String rawToken = tokenUtil.generateRawToken();
       String hash = tokenUtil.hashToken(rawToken);

        VerificationToken token = new VerificationToken();
        token.setUserId(userId);
        token.setExpiresAt(LocalDateTime.now().plus(ttl));
        token.setTokenHash(hash);
        token.setTokenType(tokenType);

        verificationTokenRepository.save(token);
        return rawToken;
    }

    public VerificationToken validateToken(String rawToken, TokenType expectedType){
        String hash = tokenUtil.hashToken(rawToken);
        var token = verificationTokenRepository.findByTokenHash(hash).orElseThrow(()->new RuntimeException("Token not found"));
        if(token.getUsedAt() != null){
            throw new RuntimeException("Token already used");
        }
        if(token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token expired");
        }

        if(token.getTokenType() != expectedType){
            throw new RuntimeException("Invalid token type");
        }

        return token;
    }

    public void markAsUsed(VerificationToken token){
        token.setUsedAt(LocalDateTime.now());
        verificationTokenRepository.save(token);
    }
}
