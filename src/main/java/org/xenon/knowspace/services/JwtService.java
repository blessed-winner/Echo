package org.xenon.knowspace.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.xenon.knowspace.config.JwtConfig;

@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;
    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
