package org.xenon.echo.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import org.xenon.echo.enums.Role;
import org.xenon.echo.exceptions.UserNotFoundException;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;

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

    public record AuthResult(
            String accessToken,
            String refreshToken,
            UUID userId
    ){}

    public AuthResult login(LoginRequest request){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            if(!user.isVerified()){
                throw new IllegalArgumentException("User is not verified");
            }
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

        String verificationToken = jwtService.generateVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(),verificationToken);
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

    public String handleVerification(String token){
        Claims claims;
        try{
            claims = jwtService.extractClaims(token);
        }catch(ExpiredJwtException e){
           throw new RuntimeException("Token has expired");
        }catch(JwtException e){
            throw new RuntimeException("Invalid token");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        var user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found"));
        if(user.isVerified()){
            return "Email already verified";
        }
        user.setVerified(true);
        userRepository.save(user);

        return "Email verification successful";
    }
}
