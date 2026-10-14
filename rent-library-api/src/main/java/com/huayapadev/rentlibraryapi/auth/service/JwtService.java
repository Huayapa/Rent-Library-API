package com.huayapadev.rentlibraryapi.auth.service;

import com.huayapadev.rentlibraryapi.auth.dto.TokenPair;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;
    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${app.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public TokenPair generateTokenPair(UserModel user) {
        String jti = UUID.randomUUID().toString();
        String accessToken = builderToken(user, jwtExpiration, jti);
        String refreshToken = builderToken(user, refreshExpiration, jti);
        return new TokenPair(accessToken, refreshToken, jti);
    }

    public String generateAccessToken(final UserModel user, String refreshTokenId) {
        return builderToken(user, jwtExpiration, refreshTokenId);
    }

    public Instant extractExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public long extractExpiresInSeconds(String token) {
        return Duration.between(Instant.now(), extractExpiration(token)).getSeconds();
    }

    public String extractTokenId(String token) {
        return parseClaims(token).getId();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private String builderToken(final UserModel user, final long expiration, String jti) {
        return Jwts.builder()
            .id(jti)
            .claim("role", user.getRole())
            .subject(user.getId().toString())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getKey())
            .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
