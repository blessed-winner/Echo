package org.xenon.knowspace.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.config.JwtConfig;
import org.xenon.knowspace.dtos.JwtResponse;
import org.xenon.knowspace.dtos.LoginRequest;
import org.xenon.knowspace.dtos.RegisterUserRequest;
import org.xenon.knowspace.dtos.UserDto;
import org.xenon.knowspace.services.AuthService;

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
        return ResponseEntity.ok(new JwtResponse(result.accessToken()));
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
     return ResponseEntity.created(uri).body(new JwtResponse(result.accessToken()));
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
            @CookieValue(name = "refreshToken") String refreshToken
    ){
        var result = authService.refresh(refreshToken);
        if(result == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new JwtResponse(result.accessToken()));
    }
}
