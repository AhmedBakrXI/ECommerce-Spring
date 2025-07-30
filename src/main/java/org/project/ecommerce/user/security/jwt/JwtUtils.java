package org.project.ecommerce.user.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Getter
@Component
public class JwtUtils {
    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    public JwtUtils(
            @Value("${spring.application.security.jwt.private-key-path}")
            String privateKeyPath,
            @Value("${spring.application.security.jwt.public-key-path}")
            String publicKeyPath
    ) {
        privateKey = KeyUtils.loadPrivateKey(privateKeyPath);
        publicKey = KeyUtils.loadPublicKey(publicKeyPath);
    }


    public String extractUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }



    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String buildToken(final String username, final Map<String, Object> claims, final long tokenExpiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(this.privateKey)
                .compact();
    }
}
