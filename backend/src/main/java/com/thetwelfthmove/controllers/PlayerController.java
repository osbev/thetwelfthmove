// Create new file: /backend/src/main/java/com/thetwelfthmove/controllers/PlayerController.java
package com.thetwelfthmove.controllers;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import com.thetwelfthmove.dao.PlayerDAO;
import com.thetwelfthmove.models.Player;

import java.util.List;
import java.util.Map;

public class PlayerController {
    private static final PlayerDAO playerDAO = new PlayerDAO();
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.get("/players/leaderboard", getLeaderboardHandler);
    }

    private static Handler getLeaderboardHandler = ctx -> {
        try {
            List<Player> players = playerDAO.getAllPlayersWithStats();
            
            ctx.json(Map.of(
                "players", players != null ? players : List.of(),
                "count", players != null ? players.size() : 0
            ));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Failed to load leaderboard"));
        }
    };
}