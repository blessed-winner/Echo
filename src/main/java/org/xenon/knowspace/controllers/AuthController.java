package org.xenon.knowspace.controllers;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.xenon.knowspace.config.JwtConfig;
import org.xenon.knowspace.dtos.JwtResponse;
import org.xenon.knowspace.dtos.LoginRequest;
import org.xenon.knowspace.repositories.UserRepository;
import org.xenon.knowspace.services.JwtService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){
            var authentication = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );

            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            var cookie = new Cookie("refreshToken", refreshToken.toString());
            cookie.setHttpOnly(true);
            cookie.setPath("/auth");
            cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
            cookie.setSecure(false);

            response.addCookie(cookie);

            return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ){
       var jwt = jwtService.parseToken(refreshToken);
       if(jwt == null || jwt.isExpired()){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }

       var user = userRepository.findById(jwt.getUserId()).orElseThrow();
       var accessToken = jwtService.generateAccessToken(user);

       return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

}
