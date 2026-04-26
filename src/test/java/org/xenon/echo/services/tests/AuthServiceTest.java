package org.xenon.echo.services.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xenon.echo.dtos.LoginRequest;
import org.xenon.echo.dtos.RegisterUserRequest;
import org.xenon.echo.entities.User;
import org.xenon.echo.entities.VerificationToken;
import org.xenon.echo.enums.AuditAction;
import org.xenon.echo.enums.Role;
import org.xenon.echo.enums.TokenType;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;
import org.xenon.echo.repositories.VerificationTokenRepository;
import org.xenon.echo.services.*;
import org.xenon.echo.utils.TokenUtil;

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
        var request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("pass");
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
        var request = new RegisterUserRequest();
        request.setName("Test Dude");
        request.setEmail("test@mail.com");
        request.setPassword("pass");
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
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("pass");
        String ip = "127.0.0.1";
        when(rateLimiterService.tryConsumeLogin(any())).thenReturn(false);
        assertThrows(RuntimeException.class,()->service.login(request,ip));
    }

    @Test
    void shouldFailWhenUserNotVerified(){
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("pass");
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

    @Test
    void shouldThrowWhenCredentialsInvalid(){
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("wrongpass");
        String ip = "127.0.0.1";

        when(rateLimiterService.tryConsumeLogin(any())).thenReturn(true);

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(manager)
                .authenticate(any());

        assertThrows(BadCredentialsException.class,()->service.login(request,ip));
        verifyNoInteractions(auditLogService);
        verifyNoInteractions(userRepo);
        verifyNoInteractions(tokenService);
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@mail.com");

        when(userRepo.existsByEmail(request.getEmail())).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,()->service.register(request));
        assertEquals("Email already exists",ex.getMessage());

        verify(userRepo, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(any(),any());
        verify(tokenService, never()).createToken(any(),any(),any());
    }

    @Test
    void shouldRequestResetSuccessfully(){
        String email = "test@mail.com";
        String ip = "127.0.0.1";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setVerified(true);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(rateLimiterService.tryConsumeReset(any())).thenReturn(true);
        when(tokenService.createToken(any(),any(),any())).thenReturn("token123");

        String result = service.requestPasswordReset(email,ip);
        assertEquals("Password reset email sent",result);

        verify(auditLogService).log(
                user.getId(),
                AuditAction.PASSWORD_RESET_REQUEST_SUCCESS,
                ip,
                true,
                null,
                null
        );

        verify(emailService).sendPasswordResetEmail(email,"token123");
    }

    @Test
    void shouldResetPasswordSuccessfully(){
       String rawToken = "token123";
       String newPassword = "newPass";

        VerificationToken token = new VerificationToken();
        UUID userId = UUID.randomUUID();
        token.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setPassword("oldEncodedPassword");


        when(tokenService.validateToken(any(),any())).thenReturn(token);
        when(userRepo.findById(token.getUserId())).thenReturn(Optional.of(user));
        when(encoder.encode(newPassword)).thenReturn("newEncodedPassword");

        var result = service.resetPassword(rawToken,newPassword);
        assertEquals("Password reset successful",result);
        assertEquals("newEncodedPassword",user.getPassword());

        verify(userRepo).save(user);
        verify(tokenService).markAsUsed(token);
        verify(tokenRepo).deleteByUserIdAndTokenType(any(),any());
        verify(auditLogService).log(
                userId,
                AuditAction.PASSWORD_RESET_SUCCESS,
                null,
                true,
                null,
                null
        );
    }
}
