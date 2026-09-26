package com.maaztausif.khallikarao.config;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds:3600}") long expirationSeconds) {

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "JWT expiration must be positive"
            );
        }

        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(long userId, long tokenVersion) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("tokenVersion", Long.toString(tokenVersion))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public TokenData readToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String version = claims.get("tokenVersion", String.class);

        if (version == null) {
            throw new IllegalArgumentException("Token version is missing");
        }

        return new TokenData(
                Long.parseLong(claims.getSubject()),
                Long.parseLong(version)
        );
    }

    public record TokenData(long userId, long tokenVersion) {
    }

    public long extractUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.parseLong(subject);
    }
}