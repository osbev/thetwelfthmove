package com.thetwelfthmove;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import com.thetwelfthmove.controller.PlayerController;
import com.thetwelfthmove.controller.GameController;
import com.thetwelfthmove.controller.StatsController;

public class App {
    public static void main(String[] args) {
        // Start Javalin server on port 7000
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH); // optional for static content
            config.enableCorsForAllOrigins(); // enable CORS for frontend
        }).start(7000);

        // Register controllers (routes)
        PlayerController.registerRoutes(app);
        GameController.registerRoutes(app);
        StatsController.registerRoutes(app);

        System.out.println("The Twelfth Move backend is running on http://localhost:7000");
    }
}
