package org.xenon.knowspace.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.xenon.knowspace.entities.Role;

import javax.crypto.SecretKey;
import java.util.Date;

public class Jwt {
    private final SecretKey secret;
    private final Claims claims;

    public Jwt(SecretKey secret, Claims claims){
        this.secret = secret;
        this.claims = claims;
    }

    public boolean isExpired(){return claims.getExpiration().before(new Date());}
    public String getUserId(){return claims.getSubject();}
    public Role getRole(){return Role.valueOf(claims.get("role",String.class));}

    public String generateToken() {
        return Jwts.builder()
                .claims(claims)
                .signWith(secret)
                .compact();
    }

    @Override
    public String toString() {
        return "Jwt{userId=" + getUserId() + ", role=" + getRole() + "}";
    }
}
