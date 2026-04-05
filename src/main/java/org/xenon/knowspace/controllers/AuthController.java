package org.xenon.knowspace.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.JwtResponse;
import org.xenon.knowspace.dtos.LoginRequest;
import org.xenon.knowspace.dtos.RegisterUserRequest;
import org.xenon.knowspace.services.AuthService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResult login(
            LoginRequest request,
            HttpServletResponse response
    ){
        return authService.login(request,response);
    }

    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
            ){
        return authService.register(request,response,uriBuilder);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }
}
