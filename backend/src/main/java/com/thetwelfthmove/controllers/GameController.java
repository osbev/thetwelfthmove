    // /backend/src/main/java/com/thetwelfthmove/controllers/GameController.java
    package com.thetwelfthmove.controllers;

    import io.javalin.Javalin;
    import io.javalin.http.Handler;
    import com.google.gson.Gson;
    import com.thetwelfthmove.dao.GameDAO;
    import com.thetwelfthmove.dao.PlayerDAO;
    import com.thetwelfthmove.models.*;
    import com.thetwelfthmove.utils.JWTUtil;

    import java.util.List;
    import java.util.Map;
    import java.util.ArrayList;

    public class GameController {
        private static final GameDAO gameDAO = new GameDAO();
        private static final PlayerDAO playerDAO = new PlayerDAO();
        private static final Gson gson = new Gson();

        public static void registerRoutes(Javalin app) {
            app.post("/games/create", createGameHandler);
            app.post("/games/create-local", createLocalGameHandler);
            app.post("/games/join", joinGameHandler);
            app.get("/games/{gameId}", getGameHandler);
            app.get("/games/code/{gameCode}", getGameByCodeHandler);
            app.post("/games/{gameId}/move", makeMoveHandler);
            app.get("/games/{gameId}/moves", getMovesHandler);
            app.get("/games/{gameId}/poll", pollGameHandler);
            app.get("/games/player/{playerId}", getPlayerGamesHandler);
            app.get("/games/player/{playerId}/recent", getRecentGamesHandler);
            app.post("/games/{gameId}/resign", resignHandler);
        }

        // Create a local 2-player game (same device, no game code needed)
        private static Handler createLocalGameHandler = ctx -> {
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

                // Create game with same player as both player1 and player2 (local mode)
                Game game = gameDAO.createLocalGame(userId);
                
                if (game != null) {
                    ctx.status(201).json(Map.of(
                        "message", "Local game created successfully",
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

        // Create a new game with unique code
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

                // Create game without player2 (multiplayer mode)
                Game game = gameDAO.createGame(userId, null);
                
                if (game != null) {
                    ctx.status(201).json(Map.of(
                        "message", "Game created successfully",
                        "game", game,
                        "gameCode", game.getGameCode()
                    ));
                } else {
                    ctx.status(500).json(Map.of("error", "Failed to create game"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
            }
        };

        // Join game by code
        private static Handler joinGameHandler = ctx -> {
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

                Map<String, String> body = gson.fromJson(ctx.body(), Map.class);
                String gameCode = body.get("gameCode");
                
                if (gameCode == null || gameCode.trim().isEmpty()) {
                    ctx.status(400).json(Map.of("error", "Game code is required"));
                    return;
                }

                // Try to join game
                boolean joined = gameDAO.joinGame(gameCode.trim().toLowerCase(), userId);
                
                if (joined) {
                    Game game = gameDAO.getGameByCode(gameCode.trim().toLowerCase());
                    ctx.json(Map.of(
                        "message", "Joined game successfully",
                        "game", game
                    ));
                } else {
                    ctx.status(400).json(Map.of("error", "Could not join game. Game may not exist, already started, or you created it."));
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
                    
                    // Get player usernames
                    Player player1 = playerDAO.findById(game.getPlayer1Id());
                    Player player2 = game.getPlayer2Id() != null ? playerDAO.findById(game.getPlayer2Id()) : null;
                    
                    ctx.json(Map.of(
                        "game", game,
                        "board", board.getBoard(),
                        "player1Username", player1 != null ? player1.getUsername() : "Unknown",
                        "player2Username", player2 != null ? player2.getUsername() : "Waiting..."
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

        // Get game by code
        private static Handler getGameByCodeHandler = ctx -> {
            try {
                String gameCode = ctx.pathParam("gameCode");
                Game game = gameDAO.getGameByCode(gameCode.toLowerCase());
                
                if (game != null) {
                    ChessBoard board = ChessBoard.fromJson(game.getBoardState());
                    
                    Player player1 = playerDAO.findById(game.getPlayer1Id());
                    Player player2 = game.getPlayer2Id() != null ? playerDAO.findById(game.getPlayer2Id()) : null;
                    
                    ctx.json(Map.of(
                        "game", game,
                        "board", board.getBoard(),
                        "player1Username", player1 != null ? player1.getUsername() : "Unknown",
                        "player2Username", player2 != null ? player2.getUsername() : "Waiting..."
                    ));
                } else {
                    ctx.status(404).json(Map.of("error", "Game not found"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).json(Map.of("error", "Internal server error: " + e.getMessage()));
            }
        };

        // Poll for game updates (for multiplayer)
        private static Handler pollGameHandler = ctx -> {
            try {
                int gameId = Integer.parseInt(ctx.pathParam("gameId"));
                Game game = gameDAO.getGameById(gameId);
                
                if (game != null) {
                    ChessBoard board = ChessBoard.fromJson(game.getBoardState());
                    List<Move> moves = gameDAO.getGameMoves(gameId);
                    
                    Player player1 = playerDAO.findById(game.getPlayer1Id());
                    Player player2 = game.getPlayer2Id() != null ? playerDAO.findById(game.getPlayer2Id()) : null;
                    
                    ctx.json(Map.of(
                        "game", game,
                        "board", board.getBoard(),
                        "moves", moves,
                        "moveCount", moves.size(),
                        "player1Username", player1 != null ? player1.getUsername() : "Unknown",
                        "player2Username", player2 != null ? player2.getUsername() : "Waiting...",
                        "lastMoveTime", game.getLastMoveTime() != null ? game.getLastMoveTime().getTime() : game.getStartTime().getTime()
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
                Map<String, Object> body = gson.fromJson(ctx.body(), Map.class);
                
                String from = (String) body.get("from");
                String to = (String) body.get("to");
                String promotionPiece = (String) body.get("promotion"); // NEW: Get promotion piece
                
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

                // Check if it's the player's turn
                String currentTurn = game.getCurrentTurn();
                boolean isPlayer1 = game.getPlayer1Id() == playerId;
                boolean isPlayer2 = game.getPlayer2Id() != null && game.getPlayer2Id() == playerId;
                
                if (!isPlayer1 && !isPlayer2) {
                    ctx.status(403).json(Map.of("error", "You are not a player in this game"));
                    return;
                }

                // Check turn
                if ((currentTurn.equals("white") && !isPlayer1) || (currentTurn.equals("black") && !isPlayer2)) {
                    ctx.status(400).json(Map.of("error", "Not your turn"));
                    return;
                }

                // Load board
                ChessBoard board = ChessBoard.fromJson(game.getBoardState());
                
                // Validate move (basic chess rules)
                if (!board.isValidMove(from, to, currentTurn)) {
                    ctx.status(400).json(Map.of("error", "Invalid move"));
                    return;
                }

                // Check if move would leave own king in check
                if (board.wouldMoveCauseCheck(from, to, currentTurn)) {
                    ctx.status(400).json(Map.of("error", "Move would leave your king in check"));
                    return;
                }

                // Get piece info before moving
                int[] fromPos = ChessBoard.notationToIndices(from);
                int[] toPos = ChessBoard.notationToIndices(to);
                ChessPiece piece = board.getPiece(fromPos[0], fromPos[1]);
                
                // Execute move
                ChessPiece capturedPiece = board.executeMove(from, to);
                
                // Handle pawn promotion
                if (piece.getType().equals("pawn")) {
                    // Check if pawn reached the end
                    boolean shouldPromote = (piece.getColor().equals("white") && toPos[0] == 0) ||
                                        (piece.getColor().equals("black") && toPos[0] == 7);
                    
                    if (shouldPromote) {
                        if (promotionPiece == null) {
                            promotionPiece = "queen"; // Default to queen
                        }
                        
                        // Validate promotion piece
                        if (!promotionPiece.equals("queen") && !promotionPiece.equals("rook") &&
                            !promotionPiece.equals("bishop") && !promotionPiece.equals("knight")) {
                            ctx.status(400).json(Map.of("error", "Invalid promotion piece"));
                            return;
                        }
                        
                        // Replace pawn with promoted piece
                        ChessPiece promotedPiece = new ChessPiece(promotionPiece, piece.getColor());
                        board.setPiece(toPos[0], toPos[1], promotedPiece);
                        
                        // Update piece type for move record
                        piece = promotedPiece;
                    }
                }
                
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
                move.setMoveNotation(from + to + (promotionPiece != null ? "=" + promotionPiece.charAt(0) : ""));
                
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
                    "board", board.getBoard(),
                    "promoted", promotionPiece != null
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

        // Get recent completed games
        private static Handler getRecentGamesHandler = ctx -> {
            try {
                int playerId = Integer.parseInt(ctx.pathParam("playerId"));
                int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
                
                List<Game> games = gameDAO.getRecentGames(playerId, limit);
                
                // Add opponent usernames
                List<Map<String, Object>> gamesWithDetails = new ArrayList<>();
                for (Game game : games) {
                    int opponentId = game.getPlayer1Id() == playerId ? 
                                    (game.getPlayer2Id() != null ? game.getPlayer2Id() : -1) : 
                                    game.getPlayer1Id();
                    
                    Player opponent = opponentId != -1 ? playerDAO.findById(opponentId) : null;
                    String opponentUsername = opponent != null ? opponent.getUsername() : "Unknown";
                    
                    boolean won = game.getWinnerId() != null && game.getWinnerId() == playerId;
                    
                    Map<String, Object> gameDetail = new java.util.HashMap<>();
                    gameDetail.put("game", game);
                    gameDetail.put("opponentUsername", opponentUsername);
                    gameDetail.put("won", won);
                    
                    gamesWithDetails.add(gameDetail);
                }
                
                ctx.json(Map.of("games", gamesWithDetails));
            } catch (NumberFormatException e) {
                ctx.status(400).json(Map.of("error", "Invalid player ID or limit"));
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