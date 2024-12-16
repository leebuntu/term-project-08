package com.leebuntu.server.communication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTManager {
    private static final String SECRET_KEY = System.getenv("JWT_SECRET");

    public static String createJWT(int userId) {
        String token = Jwts.builder()
                .setSubject("Authentication")
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();

        return token;
    }

    public static int validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            return claims.get("userId", Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }
}
