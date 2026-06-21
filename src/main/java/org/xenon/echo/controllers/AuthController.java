package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xenon.echo.config.JwtConfig;
import org.xenon.echo.dtos.*;
import org.xenon.echo.repositories.UserRepository;
import org.xenon.echo.services.AuthService;
import org.xenon.echo.services.JwtService;

import java.util.Map;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @org.springframework.beans.factory.annotation.Value("${app.cookie-secure:false}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ){
        String ip = httpRequest.getRemoteAddr();
        var result = authService.login(request,ip);
        addRefreshTokenCookie(response, result.refreshToken(), httpRequest.isSecure() || cookieSecure);
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request){
        authService.register(request);
        return ResponseEntity.status(201).body("Registration successful. Please check your email to verify your account");
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, boolean secure){
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // 7 days
        response.addCookie(cookie);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var result = authService.getMe();
        if(result == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        var result = authService.refresh(refreshToken);
        addRefreshTokenCookie(response, result.refreshToken(), request.isSecure() || cookieSecure);
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        var cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure() || cookieSecure);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(authService.handleEmailVerification(token));
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(@RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest){
        String ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.requestPasswordReset(request.getEmail(),ip));
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok(authService.resetPassword(request.getToken(),request.getNewPassword()));
    }

    @GetMapping("/success")
    public ResponseEntity<?> oauthSuccess(@RequestParam String token){
        return ResponseEntity.ok(Map.of("token",token));
    }
}
