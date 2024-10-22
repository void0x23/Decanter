package com.wine.api.api_gateway.service;

import com.wine.api.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class JwtAuthService {

    @Value("${jwt.signature.key}")
    private String signatureKey;

    private final JwtUtil jwtUtil;

    public JwtAuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public boolean isTokenValid(String token) {

        return Jwts.parser()
                .verifyWith(jwtUtil.getSignInKey(signatureKey))
                .build()
                .parseSignedClaims(token)
                .getPayload() != null;
    }

    public Jws<Claims> extractAllClaims(String token) {
        try {

            return Jwts.parser()
                    .verifyWith(jwtUtil.getSignInKey(signatureKey))
                    .build()
                    .parseSignedClaims(token);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JWT token: The token is null or improperly formatted.", e);
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token: The token may be expired or invalid.", e);
        }
    }


    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Jws<Claims> claims = extractAllClaims(token);
        return claimsResolver.apply(claims.getPayload());
    }

    public String createJwtToken(String googleId, String name, String email) {
        return jwtUtil.createJwtToken(googleId, name, email, signatureKey);
    }


}
