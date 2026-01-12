package com.retailshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails) {
        long now = System.currentTimeMillis();
        long expiry = now + jwtExpiration;

        String header = "{\"typ\":\"JWT\",\"alg\":\"HS256\"}";
        String payload = String.format(
                "{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userDetails.getUsername(),
                now / 1000,
                expiry / 1000
        );

        String signature = Base64.getEncoder().encodeToString(
                (header + "." + payload).getBytes()
        );

        return header + "." + payload + "." + signature;
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(Base64.getDecoder().decode(parts[1]));
                return payload.split("\"sub\":\"")[1].split("\"")[0];
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername());
    }
}
