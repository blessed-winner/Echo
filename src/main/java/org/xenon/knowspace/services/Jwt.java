package org.xenon.knowspace.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.xenon.knowspace.entities.Role;

import javax.crypto.SecretKey;
import java.util.Date;

public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey){
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public boolean isExpired(){
         return claims.getExpiration().before(new Date());
    }

    public String getUserId(){return String.valueOf(claims.getSubject());}
    public Role getRole(){return Role.valueOf(claims.get("role",String.class));}

    @Override
    public String toString(){
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
