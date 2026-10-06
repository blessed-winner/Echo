package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.xenon.echo.config.JwtConfig;
import org.xenon.echo.dtos.*;
import org.xenon.echo.services.AuthService;

import java.util.Map;

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
        addRefreshTokenCookie(response, result.refreshToken(), httpRequest.isSecure());
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request){
        authService.register(request);
        return ResponseEntity.status(201).body("Registration successful. Please check your email to verify your account");
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, boolean secure){
        var responseCookie = ResponseCookie.from("refreshToken", refreshToken != null ? refreshToken : "")
                .httpOnly(true)
                .secure(secure)
                .path("/auth/refresh")
                .maxAge(refreshToken != null ? jwtConfig.getRefreshTokenExpiration() : 0)
                .sameSite(secure ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
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
            @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie,
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String token = refreshTokenCookie;
        if (token == null || token.isBlank()) {
            if (body != null) {
                token = body.get("refreshToken");
                if (token == null || token.isBlank()) {
                    token = body.get("refresh_token");
                }
            }
        }
        if (token == null || token.isBlank()) {
            throw new BadCredentialsException("Refresh token is missing");
        }
        var result = authService.refresh(token);
        addRefreshTokenCookie(response, result.refreshToken(), request.isSecure());
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        addRefreshTokenCookie(response, null, request.isSecure());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(authService.handleEmailVerification(token));
    }

    @PostMapping("/forgot-password")
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
