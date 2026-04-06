package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.config.JwtConfig;
import org.xenon.knowspace.dtos.LoginRequest;
import org.xenon.knowspace.dtos.RegisterUserRequest;
import org.xenon.knowspace.dtos.UserDto;
import org.xenon.knowspace.entities.Role;
import org.xenon.knowspace.mappers.UserMapper;
import org.xenon.knowspace.repositories.UserRepository;

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
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);


            return new AuthResult(accessToken, refreshToken, user.getId());
    }

    public AuthResult register(RegisterUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResult(accessToken, refreshToken, user.getId());
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
}
