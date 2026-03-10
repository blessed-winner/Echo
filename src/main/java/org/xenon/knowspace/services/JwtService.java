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

    public Jwt generateAccessToken(User user){
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user){
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(User user, long tokenExpiration){
         var claims = Jwts.claims()
                 .subject(user.getId().toString())
                 .add("email",user.getEmail())
                 .add("name",user.getName())
                 .add("role",user.getRole().toString())
                 .issuedAt(new Date())
                 .expiration(new Date(System.currentTimeMillis() + tokenExpiration*1000))
                 .build();

         return new Jwt(claims, jwtConfig.getSecretKey());
    }

    public Jwt parseToken(String token){
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (Exception e){
            return null;
        }
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
