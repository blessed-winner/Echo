package org.xenon.echo.services.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xenon.echo.dtos.LoginRequest;
import org.xenon.echo.dtos.RegisterUserRequest;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.AuditAction;
import org.xenon.echo.enums.Role;
import org.xenon.echo.enums.TokenType;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;
import org.xenon.echo.repositories.VerificationTokenRepository;
import org.xenon.echo.services.*;

import java.time.Duration;
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

    @Test
    void shouldRegisterSuccessfully(){
        var request = new RegisterUserRequest("Test Dude","test@mail.com","pass");
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        when(userRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(encoder.encode(request.getPassword())).thenReturn("encoded");
        when(tokenService.createToken(user.getId(), TokenType.EMAIL_VERIFY, Duration.ofMinutes(15))).thenReturn("token123");

        service.register(request);

        assertEquals("encoded",user.getPassword());

        verify(userRepo).save(user);
        verify(emailService).sendVerificationEmail("blessedwinner66@gmail.com","token123");

        assertEquals(Role.USER,user.getRole());
    }

    @Test
    void shouldThrowWhenRateLimitExceeded(){
        LoginRequest request = new LoginRequest("test@mail.com","pass");
        String ip = "127.0.0.1";
        when(rateLimiterService.tryConsumeLogin(any())).thenReturn(false);
        assertThrows(RuntimeException.class,()->service.login(request,ip));
    }

    @Test
    void shouldFailWhenUserNotVerified(){
        LoginRequest request = new LoginRequest("test@mail.com","pass");
        String ip = "127.0.0.1";
        User user = new User();
        user.setEmail("test@mail.com");
        user.setVerified(false);

        when(rateLimiterService.tryConsumeLogin(any())).thenReturn(true);
        when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,()->service.login(request,ip));
        verify(auditLogService).log(
                user.getId(),
                AuditAction.LOGIN_BLOCKED_UNVERIFIED,
                ip,
                false,
                "User not verified",
                null
        );
    }
}
