package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.echo.config.JwtConfig;
import org.xenon.echo.dtos.JwtResponse;
import org.xenon.echo.dtos.LoginRequest;
import org.xenon.echo.dtos.RegisterUserRequest;
import org.xenon.echo.dtos.UserDto;
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
            HttpServletResponse response
    ){
        var result = authService.login(request);
        addRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(new JwtResponse(result.accessToken(), result.refreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterUserRequest request,
            HttpServletResponse response,
            UriComponentsBuilder uriBuilder
            ){
     var result = authService.register(request);
     addRefreshTokenCookie(response, result.refreshToken());
     var uri = uriBuilder.path("/auth").buildAndExpand(result.userId()).toUri();
     return ResponseEntity.created(uri).body(new JwtResponse(result.accessToken(), result.refreshToken()));
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
}
