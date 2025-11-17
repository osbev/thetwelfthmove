package com.thetwelfthmove;

import io.javalin.Javalin;
import com.thetwelfthmove.controllers.AuthController;
import com.thetwelfthmove.dao.DatabaseConnection;

public class App {
    public static void main(String[] args) {
        // Initialize database and tables
        try {
            DatabaseConnection.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
            System.err.println("The application will continue, but database operations may fail.");
        }

        // Start Javalin server on port 7000
        Javalin app = Javalin.create(config -> {
            // Enable CORS for all origins (for React frontend)
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        }).start(7000);

        // Register authentication routes
        AuthController.registerRoutes(app);

        System.out.println("The Twelfth Move backend is running on http://localhost:7000");

        // Optional: shutdown hook to close DB connection when app stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down backend...");
            DatabaseConnection.closeConnection();
        }));
    }
}
