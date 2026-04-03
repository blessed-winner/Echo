package org.xenon.knowspace.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.config.JwtConfig;
import org.xenon.knowspace.entities.User;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    private String generateAccessToken(User user){}

    public Jwt generateToken(User user, Long tokenExpiration){
             var claims = Jwts.claims()
                     .subject(user.getId())
                     .add("email",user.getEmail())
                     .add("role",user.getRole())
                     .issuedAt(new Date())
                     .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                     .build();
             return new Jwt(jwtConfig.getSecretKey(), claims);
    }
}
