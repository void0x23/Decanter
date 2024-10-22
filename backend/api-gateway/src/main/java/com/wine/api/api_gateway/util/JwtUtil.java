package com.wine.api.api_gateway.util;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    public SecretKey getSignInKey(String secretKey) {
        byte[] bytes = Base64.getDecoder()
                .decode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    public String createJwtToken(String googleId, String name, String email, String signatureKey) {

        long expirationDate = 1000 * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .claim("googleId", googleId)
                .claim("name", name)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationDate))
                .signWith(getSignInKey(signatureKey))
                .compact();
    }

}



