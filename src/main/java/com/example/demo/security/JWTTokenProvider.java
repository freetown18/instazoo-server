package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JWTTokenProvider.class);
    private final SecretKey secretKey;

    public JWTTokenProvider(@Value("${jwt.secret}") String secretBase64) {
        try {
            if (secretBase64 == null || secretBase64.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret is not configured. Set 'jwt.secret' in properties.");
            }

            byte[] decodedKey = Base64.getDecoder().decode(secretBase64);
            if (decodedKey.length < 64) {
                throw new IllegalArgumentException(
                        "JWT key must be at least 64 bytes (512 bits) for HS512. Current: " + decodedKey.length + " bytes."
                );
            }

            this.secretKey = new SecretKeySpec(decodedKey, "HmacSHA512");
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid JWT secret configuration. Base64 decoding failed.", e);
            throw e;
        }
    }

    // Преобразуем SECRET в безопасный ключ для HMAC-SHA512 - способ для строки в конфиг файле
//    public JWTTokenProvider() {
//        this.secretKey = Keys.hmacShaKeyFor(SecurityConstants.SECRET.getBytes());
//    }

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", user.getId());
        claimsMap.put("username", user.getEmail());
        claimsMap.put("firstname", user.getName());
        claimsMap.put("lastname", user.getLastname());

        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .claims(claimsMap)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            LOG.error("JWT validation error: {}", ex.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("id", Long.class);
    }
}