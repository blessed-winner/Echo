package org.xenon.echo.services.tests;


import org.junit.jupiter.api.*;
import org.xenon.echo.entities.VerificationToken;
import org.xenon.echo.enums.TokenType;
import org.xenon.echo.repositories.VerificationTokenRepository;
import org.xenon.echo.services.VerificationTokenService;
import org.xenon.echo.utils.TokenUtil;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VerificationTokenServiceTest {
    private VerificationTokenRepository tokenRepo;
    private TokenUtil tokenUtil;
    private VerificationTokenService service;

    @BeforeEach
    void setup(){
        tokenRepo = mock(VerificationTokenRepository.class);
        tokenUtil = mock(TokenUtil.class);

        service = new VerificationTokenService(tokenRepo,tokenUtil);
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        String rawToken = "bad-token";
        when(tokenUtil.hashToken(any())).thenReturn("hashed");
        when(tokenRepo.findByTokenHash("hashed")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,()->{
           service.validateToken(rawToken, TokenType.PASSWORD_RESET);
        });
    }

    @Test
    void shouldThrowWhenTokenAlreadyUsed(){
        String rawToken = "used-token";

        VerificationToken token = new VerificationToken();
        token.setUsedAt(LocalDateTime.now());

        when(tokenUtil.hashToken(any())).thenReturn("hashed");
        when(tokenRepo.findByTokenHash("hashed")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, ()->{
            service.validateToken(rawToken, TokenType.PASSWORD_RESET);
        });
    }

    @Test
    void shouldThrowWhenTokenExpired(){
        String rawToken = "expired-token";

        VerificationToken token = new VerificationToken();
        token.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(tokenUtil.hashToken(any())).thenReturn("hashed");
        when(tokenRepo.findByTokenHash("hashed")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class, ()->{
            service.validateToken(rawToken, TokenType.PASSWORD_RESET);
        });
    }

    @Test
    void shouldThrowWhenTokenTypeInvalid(){
        String rawToken = "invalid-token";

        VerificationToken token = new VerificationToken();
        token.setTokenType(TokenType.EMAIL_VERIFY);

        when(tokenUtil.hashToken(any())).thenReturn("hashed");
        when(tokenRepo.findByTokenHash("hashed")).thenReturn(Optional.of(token));
        assertThrows(RuntimeException.class, ()->{
            service.validateToken(rawToken, TokenType.PASSWORD_RESET);
        });
    }
}
