package com.thetwelfthmove.controllers;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import com.thetwelfthmove.dao.PlayerDAO;
import com.thetwelfthmove.models.Player;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Map;

public class AuthController {

    private static final PlayerDAO playerDAO = new PlayerDAO();
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.post("/signup", registerHandler);
        app.post("/login", loginHandler);
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
                ctx.status(201).json(Map.of("message", "Player registered successfully."));
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
                ctx.json(Map.of("message", "Login successful.", "user", Map.of("id", player.getId(), "username", player.getUsername())));
            } else {
                ctx.status(401).json(Map.of("error", "Invalid password."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Password strength validation
    private static boolean isPasswordStrong(String password) {
        if (password.length() < 6) return false;
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }

}
