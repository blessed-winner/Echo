package org.xenon.echo.services.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xenon.echo.dtos.LoginRequest;
import org.xenon.echo.dtos.RegisterUserRequest;
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
    private AuthenticationManager manager;
    private UserRepository userRepo;
    private JwtService jwtService;
    private AuditLogService auditLogService;
    private VerificationTokenRepository tokenRepo;
    private RateLimiterService rateLimiterService;
    private VerificationTokenService tokenService;
    private EmailService emailService;
    private UserMapper userMapper;
    private PasswordEncoder encoder;

    private AuthService service;

    @BeforeEach
    void setup() {
        manager = mock(AuthenticationManager.class);
        userRepo = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        auditLogService = mock(AuditLogService.class);
        tokenRepo = mock(VerificationTokenRepository.class);
        rateLimiterService = mock(RateLimiterService.class);
        tokenService = mock(VerificationTokenService.class);
        emailService = mock(EmailService.class);
        userMapper = mock(UserMapper.class);
        encoder = mock(PasswordEncoder.class);

        service = new AuthService(
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
    }


    @Test
    void shouldLoginSuccessfully(){
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

    void shouldRegisterSuccessfully(){
        var request = new RegisterUserRequest("Test Dude","test@mail.com","pass");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
    }
}
