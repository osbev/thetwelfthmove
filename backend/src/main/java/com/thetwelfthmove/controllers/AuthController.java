// /backend/src/main/java/com/thetwelfthmove/controllers/AuthController.java
package com.thetwelfthmove.controllers;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import com.thetwelfthmove.dao.PlayerDAO;
import com.thetwelfthmove.models.Player;
import com.thetwelfthmove.utils.JWTUtil;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Map;

public class AuthController {

    private static final PlayerDAO playerDAO = new PlayerDAO();
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.post("/signup", registerHandler);
        app.post("/login", loginHandler);
        app.post("/verify-token", verifyTokenHandler);
        app.post("/logout", logoutHandler);
    }

    // Signup handler
    private static Handler registerHandler = ctx -> {
        try {
            Map<String, String> body = gson.fromJson(ctx.body(), Map.class);
            String username = body.get("username");
            String password = body.get("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Username and password cannot be empty."));
                return;
            }

            if (!isPasswordStrong(password)) {
                ctx.status(400).json(Map.of("error", "Password must be at least 6 characters and include letters and numbers."));
                return;
            }

            boolean success = playerDAO.createPlayer(username, password);

            if (success) {
                // Fetch the newly created player to get their ID
                Player player = playerDAO.findByUsername(username);
                
                // Generate JWT token
                String token = JWTUtil.generateToken(player.getId(), player.getUsername());
                
                ctx.status(201).json(Map.of(
                    "message", "Player registered successfully.",
                    "token", token,
                    "user", Map.of(
                        "id", player.getId(),
                        "username", player.getUsername(),
                        "gamesPlayed", player.getGamesPlayed(),
                        "gamesWon", player.getGamesWon()
                    )
                ));
            } else {
                ctx.status(400).json(Map.of("error", "Username already exists."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Login handler
    private static Handler loginHandler = ctx -> {
        try {
            Map<String, String> body = gson.fromJson(ctx.body(), Map.class);
            String username = body.get("username");
            String password = body.get("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Username and password cannot be empty."));
                return;
            }

            Player player = playerDAO.findByUsername(username);
            if (player == null) {
                ctx.status(404).json(Map.of("error", "User not found."));
                return;
            }

            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), player.getPassword());
            if (result.verified) {
                // Generate JWT token
                String token = JWTUtil.generateToken(player.getId(), player.getUsername());
                
                ctx.json(Map.of(
                    "message", "Login successful.",
                    "token", token,
                    "user", Map.of(
                        "id", player.getId(),
                        "username", player.getUsername(),
                        "gamesPlayed", player.getGamesPlayed(),
                        "gamesWon", player.getGamesWon()
                    )
                ));
            } else {
                ctx.status(401).json(Map.of("error", "Invalid password."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Verify token handler - for frontend to check if token is still valid
    private static Handler verifyTokenHandler = ctx -> {
        try {
            String authHeader = ctx.header("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).json(Map.of("error", "Missing or invalid token."));
                return;
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            int userId = JWTUtil.getUserIdFromToken(token);
            String username = JWTUtil.getUsernameFromToken(token);
            
            if (userId == -1 || username == null) {
                ctx.status(401).json(Map.of("error", "Invalid or expired token."));
                return;
            }

            // Fetch fresh user data
            Player player = playerDAO.findByUsername(username);
            if (player == null) {
                ctx.status(404).json(Map.of("error", "User not found."));
                return;
            }

            ctx.json(Map.of(
                "valid", true,
                "user", Map.of(
                    "id", player.getId(),
                    "username", player.getUsername(),
                    "gamesPlayed", player.getGamesPlayed(),
                    "gamesWon", player.getGamesWon()
                )
            ));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Logout handler - for future use (client-side will handle token removal)
    private static Handler logoutHandler = ctx -> {
        // In JWT, logout is handled client-side by removing the token
        // This endpoint is here for future enhancements like token blacklisting
        ctx.json(Map.of("message", "Logout successful."));
    };

    // Password strength validation
    private static boolean isPasswordStrong(String password) {
        if (password.length() < 6) return false;
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }

}