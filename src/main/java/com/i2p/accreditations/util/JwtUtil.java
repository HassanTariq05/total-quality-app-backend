package com.i2p.accreditations.util;

import com.i2p.accreditations.model.access.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final long EXPIRATION_MS = 86400000;

    private final String SECRET = Base64.getEncoder()
            .encodeToString("A1HD-3EFA-F2A1-CBF8A1HD-2DEZ-F2A1-CBF8".getBytes());

    /** Generate JWT with USER ID as subject */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString()) // USER ID
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /** Extract USER ID from JWT subject */
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    /** Optional: extract email if needed */
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    /** Validate token integrity + expiration */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
