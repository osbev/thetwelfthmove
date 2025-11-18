// /backend/src/main/java/com/thetwelfthmove/controllers/GameController.java
package com.thetwelfthmove.controllers;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import com.thetwelfthmove.dao.GameDAO;
import com.thetwelfthmove.models.*;
import com.thetwelfthmove.utils.JWTUtil;

import java.util.List;
import java.util.Map;

public class GameController {
    private static final GameDAO gameDAO = new GameDAO();
    private static final Gson gson = new Gson();

    public static void registerRoutes(Javalin app) {
        app.post("/games/create", createGameHandler);
        app.get("/games/{gameId}", getGameHandler);
        app.post("/games/{gameId}/move", makeMoveHandler);
        app.get("/games/{gameId}/moves", getMovesHandler);
        app.get("/games/player/{playerId}", getPlayerGamesHandler);
        app.post("/games/{gameId}/resign", resignHandler);
    }

    // Create a new game
    private static Handler createGameHandler = ctx -> {
        try {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
                return;
            }

            String token = authHeader.substring(7);
            int userId = JWTUtil.getUserIdFromToken(token);
            
            if (userId == -1) {
                ctx.status(401).json(Map.of("error", "Invalid token"));
                return;
            }

            // For local 2-player game, player2Id is null initially
            // They'll both use same device and authenticate
            Game game = gameDAO.createGame(userId, null);
            
            if (game != null) {
                ctx.status(201).json(Map.of(
                    "message", "Game created successfully",
                    "game", game
                ));
            } else {
                ctx.status(500).json(Map.of("error", "Failed to create game"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Get game state
    private static Handler getGameHandler = ctx -> {
        try {
            int gameId = Integer.parseInt(ctx.pathParam("gameId"));
            Game game = gameDAO.getGameById(gameId);
            
            if (game != null) {
                // Parse board state and include it
                ChessBoard board = ChessBoard.fromJson(game.getBoardState());
                
                ctx.json(Map.of(
                    "game", game,
                    "board", board.getBoard()
                ));
            } else {
                ctx.status(404).json(Map.of("error", "Game not found"));
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid game ID"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Make a move
    private static Handler makeMoveHandler = ctx -> {
        try {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
                return;
            }

            String token = authHeader.substring(7);
            int playerId = JWTUtil.getUserIdFromToken(token);
            
            if (playerId == -1) {
                ctx.status(401).json(Map.of("error", "Invalid token"));
                return;
            }

            int gameId = Integer.parseInt(ctx.pathParam("gameId"));
            Map<String, String> body = gson.fromJson(ctx.body(), Map.class);
            
            String from = body.get("from");
            String to = body.get("to");
            
            if (from == null || to == null) {
                ctx.status(400).json(Map.of("error", "Missing 'from' or 'to' fields"));
                return;
            }

            // Get current game state
            Game game = gameDAO.getGameById(gameId);
            if (game == null) {
                ctx.status(404).json(Map.of("error", "Game not found"));
                return;
            }

            if (!game.getStatus().equals("ongoing")) {
                ctx.status(400).json(Map.of("error", "Game is not ongoing"));
                return;
            }

            // Load board
            ChessBoard board = ChessBoard.fromJson(game.getBoardState());
            
            // Validate it's the correct player's turn
            String currentTurn = game.getCurrentTurn();
            
            // Validate move
            if (!board.isValidMove(from, to, currentTurn)) {
                ctx.status(400).json(Map.of("error", "Invalid move"));
                return;
            }

            // Get piece info before moving
            int[] fromPos = ChessBoard.notationToIndices(from);
            ChessPiece piece = board.getPiece(fromPos[0], fromPos[1]);
            
            // Execute move
            ChessPiece capturedPiece = board.executeMove(from, to);
            
            // Check for check/checkmate
            String opponentColor = currentTurn.equals("white") ? "black" : "white";
            boolean isCheck = board.isKingInCheck(opponentColor);
            boolean isCheckmate = board.isCheckmate(opponentColor);
            
            // Get current move count
            List<Move> existingMoves = gameDAO.getGameMoves(gameId);
            int moveNumber = existingMoves.size() + 1;
            
            // Save move to database
            Move move = new Move(gameId, playerId, moveNumber, from, to, piece.getType(), piece.getColor());
            if (capturedPiece != null) {
                move.setCapturedPiece(capturedPiece.getType());
            }
            move.setCheck(isCheck);
            move.setCheckmate(isCheckmate);
            move.setMoveNotation(from + to); // Simple notation for now
            
            gameDAO.saveMove(move);
            
            // Update game state
            String nextTurn = currentTurn.equals("white") ? "black" : "white";
            gameDAO.updateGameState(gameId, board.toJson(), nextTurn);
            
            // If checkmate, end game
            if (isCheckmate) {
                String result = currentTurn.equals("white") ? "white_wins" : "black_wins";
                gameDAO.endGame(gameId, result, playerId);
            }
            
            // Return updated state
            ctx.json(Map.of(
                "message", "Move executed successfully",
                "move", move,
                "isCheck", isCheck,
                "isCheckmate", isCheckmate,
                "nextTurn", nextTurn,
                "board", board.getBoard()
            ));
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid game ID"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Get all moves for a game
    private static Handler getMovesHandler = ctx -> {
        try {
            int gameId = Integer.parseInt(ctx.pathParam("gameId"));
            List<Move> moves = gameDAO.getGameMoves(gameId);
            
            ctx.json(Map.of("moves", moves));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid game ID"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Get player's games
    private static Handler getPlayerGamesHandler = ctx -> {
        try {
            int playerId = Integer.parseInt(ctx.pathParam("playerId"));
            String status = ctx.queryParam("status") != null ? ctx.queryParam("status") : "ongoing";
            
            List<Game> games = gameDAO.getPlayerGames(playerId, status);
            
            ctx.json(Map.of("games", games));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid player ID"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };

    // Resign from game
    private static Handler resignHandler = ctx -> {
        try {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).json(Map.of("error", "Unauthorized"));
                return;
            }

            String token = authHeader.substring(7);
            int playerId = JWTUtil.getUserIdFromToken(token);
            
            if (playerId == -1) {
                ctx.status(401).json(Map.of("error", "Invalid token"));
                return;
            }

            int gameId = Integer.parseInt(ctx.pathParam("gameId"));
            Game game = gameDAO.getGameById(gameId);
            
            if (game == null) {
                ctx.status(404).json(Map.of("error", "Game not found"));
                return;
            }

            // Determine winner (the other player)
            int winnerId = game.getPlayer1Id() == playerId ? 
                          (game.getPlayer2Id() != null ? game.getPlayer2Id() : game.getPlayer1Id()) : 
                          game.getPlayer1Id();
            
            String result = game.getPlayer1Id() == playerId ? "black_wins" : "white_wins";
            
            gameDAO.endGame(gameId, result, winnerId);
            
            ctx.json(Map.of("message", "Game resigned", "winner", winnerId));
            
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid game ID"));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    };
}