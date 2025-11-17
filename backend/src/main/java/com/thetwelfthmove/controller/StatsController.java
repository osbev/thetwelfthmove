package com.thetwelfthmove.controller;

import com.thetwelfthmove.dao.PlayerDAO;
import com.thetwelfthmove.model.Player;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import java.sql.SQLException;
import java.util.List;

public class StatsController {
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.get("/stats", getStats);
    }

    private static Handler getStats = ctx -> {
        try {
            List<Player> players = PlayerDAO.getAllPlayers();
            // Simple stats: total players, total games played
            int totalPlayers = players.size();
            int totalGames = players.stream().mapToInt(Player::getGamesPlayed).sum();
            ctx.json("{\"totalPlayers\": " + totalPlayers + ", \"totalGames\": " + totalGames + "}");
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };
}
