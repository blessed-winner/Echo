package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xenon.echo.config.JwtConfig;
import org.xenon.echo.dtos.LoginRequest;
import org.xenon.echo.dtos.RegisterUserRequest;
import org.xenon.echo.dtos.UserDto;
import org.xenon.echo.entities.User;
import org.xenon.echo.entities.VerificationToken;
import org.xenon.echo.enums.AuditAction;
import org.xenon.echo.enums.Role;
import org.xenon.echo.enums.TokenType;
import org.xenon.echo.exceptions.UserNotFoundException;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;
import org.xenon.echo.repositories.VerificationTokenRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository tokenRepository;
    private final VerificationTokenService verificationTokenService;
    private final RateLimiterService rateLimiterService;
    private final AuditLogService auditLogService;

    public record AuthResult(
            String accessToken,
            String refreshToken,
            UUID userId
    ){}

    public AuthResult login(LoginRequest request, String ip){
            String key = ip + "_" + request.getEmail();

            if(!rateLimiterService.tryConsumeLogin(key)){
                throw new RuntimeException("Too many login attempts.Try again later");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            if(!user.isVerified()){
                auditLogService.log(
                        user.getId(),
                        AuditAction.LOGIN_BLOCKED_UNVERIFIED,
                        ip,
                        false,
                        "User not verified",
                        null
                );
                throw new IllegalArgumentException("Invalid credentials");
            }
            auditLogService.log(
                    user.getId(),
                    AuditAction.LOGIN_SUCCESS,
                    ip,
                    true,
                    null,
                    null
            );
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);


            return new AuthResult(accessToken, refreshToken, user.getId());
    }

    public void register(RegisterUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = verificationTokenService.createToken(user.getId(),TokenType.EMAIL_VERIFY,Duration.ofMinutes(15));
        emailService.sendVerificationEmail("blessedwinner66@gmail.com",token);
    }

    public UserDto getMe(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (UUID) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return null;
        }

        return userMapper.toDto(user);
    }

    public AuthResult refresh(String refreshToken){
       if(!jwtService.isTokenValid(refreshToken)){
           throw new IllegalArgumentException("Refresh token is invalid");
       }

       var userId = jwtService.extractUserId(refreshToken);
       var user = userRepository.findById(userId).orElseThrow();

       var accessToken = jwtService.generateAccessToken(user);
       var newRefreshToken = jwtService.generateRefreshToken(user);

       return new AuthResult(accessToken, newRefreshToken, user.getId());
    }

    public String handleEmailVerification(String rawToken){
        VerificationToken token = verificationTokenService.validateToken(rawToken, TokenType.EMAIL_VERIFY);
        var user = userRepository.findById(token.getUserId()).orElseThrow(()->new UserNotFoundException("User not found"));
        if(user.isVerified()){
            auditLogService.log(
                    user.getId(),
                    AuditAction.EMAIL_VERIFICATION_ALREADY_DONE,
                    null,
                    false,
                    "User already verified",
                    null
            );
            return "User already verified";
        }
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenService.markAsUsed(token);
        tokenRepository.deleteByUserIdAndTokenType(user.getId(),TokenType.EMAIL_VERIFY);

        auditLogService.log(
                user.getId(),
                AuditAction.EMAIL_VERIFICATION_SUCCESS,
                null,
                true,
                null,
                null
        );
        return "Email verification successful";
    }

    public String requestPasswordReset(String email,String ip){
        String key = ip + "_" + email;
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null){
            return "If user exists, a reset email has been sent";
        }
        if(!rateLimiterService.tryConsumeReset(key)){
            auditLogService.log(
                    user.getId(),
                    AuditAction.RATE_LIMIT_HIT,
                    ip,
                    false,
                    "Too many reset attempts",
                    null
            );
            throw new RuntimeException("Too many reset attempts.Try again later");
        }
        if(!user.isVerified()){
            auditLogService.log(
                    user.getId(),
                    AuditAction.PASSWORD_RESET_REQUEST_FAILED,
                    ip,
                    false,
                    "User not verified",
                    null
            );
            throw new RuntimeException("User not verified.Verify first");
        }

        auditLogService.log(
                user.getId(),
                AuditAction.PASSWORD_RESET_REQUEST_SUCCESS,
                ip,
                true,
                null,
                null
        );

        String token = verificationTokenService.createToken(user.getId(),TokenType.PASSWORD_RESET, Duration.ofMinutes(15));
        emailService.sendPasswordResetEmail(email,token);
        return "Password reset email sent";
    }

    public String resetPassword(String rawToken, String newPassword){
        VerificationToken token = verificationTokenService.validateToken(rawToken,TokenType.PASSWORD_RESET);
        User user = userRepository.findById(token.getUserId()).orElseThrow(()->new UserNotFoundException("User not found"));
        auditLogService.log(
                user.getId(),
                AuditAction.PASSWORD_RESET_SUCCESS,
                null,
                true,
                null,
                null
        );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        verificationTokenService.markAsUsed(token);
        tokenRepository.deleteByUserIdAndTokenType(user.getId(),TokenType.PASSWORD_RESET);
        return "Password reset successful";
    }
}
