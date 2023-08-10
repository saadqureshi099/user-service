package com.instagram.User.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    /**
     * Generates a token based on a successfull login
     * @param username
     * @return
     */
    public String generateToken(String username){
        log.info("generating token");
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,username);
    }

    /**
     * Creates a token by setting all fields of the jwt and signs it with the key
     * @param claims
     * @param username
     * @return
     */
    public String createToken(Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * This methods returns the sign key used to Encrypt the Header and Payload in Jwt Token
     * @return
     */
    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
