package org.xenon.knowspace.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.config.JwtConfig;
import org.xenon.knowspace.dtos.JwtResponse;
import org.xenon.knowspace.dtos.LoginRequest;
import org.xenon.knowspace.dtos.RegisterUserRequest;
import org.xenon.knowspace.repositories.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            Cookie cookie = new Cookie("refreshToken",refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/auth");
            cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());

            response.addCookie(cookie);

            return ResponseEntity.ok(new JwtResponse(accessToken));
        }catch (Exception e){
            return ResponseEntity.status(401).build();
        }
    }

    public ResponseEntity<JwtResponse> register(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
            ){
    }
}
