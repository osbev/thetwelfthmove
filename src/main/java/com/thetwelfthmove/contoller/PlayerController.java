package com.thetwelfthmove.controller;

import com.thetwelfthmove.dao.PlayerDAO;
import com.thetwelfthmove.model.Player;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import java.sql.SQLException;
import java.util.List;

public class PlayerController {
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.get("/players", getPlayers);
        app.post("/players", createPlayer);
    }

    private static Handler getPlayers = ctx -> {
        try {
            List<Player> players = PlayerDAO.getAllPlayers();
            ctx.json(players);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };

    private static Handler createPlayer = ctx -> {
        Player p = gson.fromJson(ctx.body(), Player.class);
        try {
            PlayerDAO.createPlayer(p.getUsername(), p.getPassword());
            ctx.status(201).json(p);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };
}
