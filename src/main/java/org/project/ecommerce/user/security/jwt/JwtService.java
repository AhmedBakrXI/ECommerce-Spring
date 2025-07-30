package org.project.ecommerce.user.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
//    private final String secret;
    private static final String TOKEN_TYPE = "TOKEN_TYPE";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS_TOKEN_TYPE";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH_TOKEN_TYPE";

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${spring.application.security.jwt.access-token-expiration-time}")
    private long accessTokenExpiresInMs;

    @Value("${spring.application.security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpiresInMs;


    public String generateAccessToken(final String username) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, ACCESS_TOKEN_TYPE);
        return buildToken(username, claims, accessTokenExpiresInMs);
    }

    public String generateRefreshToken(final String username) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, REFRESH_TOKEN_TYPE);
        return buildToken(username, claims, refreshTokenExpiresInMs);
    }

    public String refreshAccessToken(final String refreshToken) {
        Claims claims = jwtUtils.extractClaims(refreshToken);
        if (!REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE))) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        if (jwtUtils.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }
        return generateAccessToken(claims.getSubject());
    }

    public boolean validateAccessToken(final String token, final String expectedUsername) {
        String username = jwtUtils.extractUsernameFromToken(token);
        boolean isUsernameAsExpected = expectedUsername.equals(username);
        if (!isUsernameAsExpected) {
            return false;
        }
        return !jwtUtils.isTokenExpired(token);
    }

    public String extractUsernameFromToken(final String token) {
        return jwtUtils.extractUsernameFromToken(token);
    }

    private String buildToken(final String username, final Map<String, Object> claims, final long tokenExpiration) {
        return jwtUtils.buildToken(username, claims, tokenExpiration);
    }
}
