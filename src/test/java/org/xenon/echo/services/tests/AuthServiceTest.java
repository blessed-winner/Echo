package org.xenon.echo.services.tests;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xenon.echo.dtos.LoginRequest;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.AuditAction;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;
import org.xenon.echo.repositories.VerificationTokenRepository;
import org.xenon.echo.services.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Test
    void shouldLoginSuccessfully(){
        AuthenticationManager manager = mock(AuthenticationManager.class);
        UserRepository userRepo = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);
        AuditLogService auditLogService = mock(AuditLogService.class);
        VerificationTokenRepository tokenRepo = mock(VerificationTokenRepository.class);
        RateLimiterService rateLimiterService = mock(RateLimiterService.class);
        VerificationTokenService tokenService = mock(VerificationTokenService.class);
        EmailService emailService = mock(EmailService.class);
        UserMapper userMapper = mock(UserMapper.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        AuthService service = new AuthService(
                manager,
                userRepo,
                jwtService,
                userMapper,
                encoder,
                emailService,
                tokenRepo,
                tokenService,
                rateLimiterService,
                auditLogService
        );

        var request = new LoginRequest("test@mail.com","pass");
        String ip = "127.0.0.1";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("test@mail.com");
        user.setVerified(true);

        when(rateLimiterService.tryConsumeLogin(any())).thenReturn(true);
        when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        var result = service.login(request,ip);

        assertEquals("access",result.accessToken());
        assertEquals("refresh",result.refreshToken());
        assertEquals(userId,result.userId());

        verify(auditLogService).log(
                userId,
                AuditAction.LOGIN_SUCCESS,
                ip,
                true,
                null,
                null
        );
    }
}
