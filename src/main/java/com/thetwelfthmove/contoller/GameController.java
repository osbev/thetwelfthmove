package com.thetwelfthmove.controller;

import com.google.gson.Gson;
import com.thetwelfthmove.dao.GameDAO;
import com.thetwelfthmove.model.Game;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import java.sql.SQLException;
import java.util.List;

public class GameController {
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.get("/games", getGames);
        app.post("/games", createGame);
    }

    private static Handler getGames = ctx -> {
        try {
            List<Game> games = GameDAO.getAllGames();
            ctx.json(games);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };

    private static Handler createGame = ctx -> {
        Game game = gson.fromJson(ctx.body(), Game.class);
        try {
            GameDAO.createGame(game.getPlayer1Id(), game.getPlayer2Id());
            ctx.status(201).json(game);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };
}
