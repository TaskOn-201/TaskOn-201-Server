package com.twohundredone.taskonserver.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private final Key key;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtProvider(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.access-token-validity-ms}") long accessTokenValidityMs,   // 15분
            @Value("${spring.jwt.refresh-token-validity-ms}") long refreshTokenValidityMs // 14일
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    public String createAccessToken(Long userId, String email) {
        return createToken(userId, email, accessTokenValidityMs);
    }

    public String createRefreshToken(Long userId, String email) {
        return createToken(userId, email, refreshTokenValidityMs);
    }

    private String createToken(Long userId, String email, long validityMs) {
        long now = System.currentTimeMillis();
        Date expiry = new Date(now + validityMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date(now))
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getBody().getSubject());
    }

    public String getEmail(String token) {
        return parseToken(token).getBody().get("email", String.class);
    }
}
