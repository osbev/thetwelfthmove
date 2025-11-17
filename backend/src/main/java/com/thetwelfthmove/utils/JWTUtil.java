// /backend/src/main/java/com/thetwelfthmove/utils/JWTUtil.java
package com.thetwelfthmove.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtil {
    
    // SECRET KEY - In production, store this in environment variables or config file
    private static final String SECRET_KEY = "your-secret-key-change-this-in-production-12345";
    
    // Token expiration time: 7 days in milliseconds
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    /**
     * Generate JWT token for a user
     * @param userId User's ID
     * @param username User's username
     * @return JWT token string
     */
    public static String generateToken(int userId, String username) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    /**
     * Verify and decode JWT token
     * @param token JWT token string
     * @return DecodedJWT object if valid, null if invalid
     */
    public static DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * Extract user ID from token
     * @param token JWT token string
     * @return User ID, or -1 if invalid
     */
    public static int getUserIdFromToken(String token) {
        DecodedJWT decoded = verifyToken(token);
        if (decoded != null) {
            return Integer.parseInt(decoded.getSubject());
        }
        return -1;
    }

    /**
     * Extract username from token
     * @param token JWT token string
     * @return Username, or null if invalid
     */
    public static String getUsernameFromToken(String token) {
        DecodedJWT decoded = verifyToken(token);
        if (decoded != null) {
            return decoded.getClaim("username").asString();
        }
        return null;
    }

    /**
     * Check if token is expired
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        DecodedJWT decoded = verifyToken(token);
        if (decoded != null) {
            return decoded.getExpiresAt().before(new Date());
        }
        return true;
    }
}