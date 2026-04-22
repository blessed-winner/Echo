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
import org.xenon.echo.services.AuthService;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ){
        String ip = httpRequest.getRemoteAddr();
        var result = authService.login(request,ip);
        addRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request){
        authService.register(request);
        return ResponseEntity.status(201).body("Registration successful. Please check your email to verify your account");
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
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
            HttpServletResponse response
    ){
        var result = authService.refresh(refreshToken);
        addRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
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
}
