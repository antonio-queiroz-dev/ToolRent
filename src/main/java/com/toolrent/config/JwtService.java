package com.toolrent.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final String ISSUER = "toolrent";

    private final SecretKey key;
    private final long expirationSeconds;

    // construtor
    public JwtService(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = properties.expiration().toSeconds();
    }

    // gera token
    public String generate(UUID userId, UUID tenantId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(userId.toString())
                .claim("tenant_id", tenantId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }
}
