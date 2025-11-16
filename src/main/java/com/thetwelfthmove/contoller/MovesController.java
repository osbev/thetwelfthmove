package com.thetwelfthmove.contoller;

public package com.thetwelfthmove.controller;

import com.google.gson.Gson;
import com.thetwelfthmove.dao.MoveDAO;
import com.thetwelfthmove.model.Move;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import java.sql.SQLException;
import java.util.List;

public class MovesController {
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.get("/moves/{gameId}", getMovesByGame);
        app.post("/moves", addMove);
    }

    private static Handler getMovesByGame = ctx -> {
        int gameId = Integer.parseInt(ctx.pathParam("gameId"));
        try {
            List<Move> moves = MoveDAO.getMovesByGame(gameId);
            ctx.json(moves);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };

    private static Handler addMove = ctx -> {
        Move move = gson.fromJson(ctx.body(), Move.class);
        try {
            MoveDAO.addMove(move);
            ctx.status(201).json(move);
        } catch (SQLException e) {
            ctx.status(500).result("Database error: " + e.getMessage());
        }
    };
}
 {
    
}
