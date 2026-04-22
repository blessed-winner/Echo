package org.xenon.echo.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.config.JwtConfig;
import org.xenon.echo.entities.User;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user){
        return buildToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User user){
        return buildToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private String buildToken(User user, long tokenExpiration){
        Date expiry = new Date(new Date().getTime() + tokenExpiration * 1000);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(expiry)
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token){
        try{
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID extractUserId(String token){
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractUserRole(String token){
        return extractClaims(token).get("role",String.class);
    }
}
