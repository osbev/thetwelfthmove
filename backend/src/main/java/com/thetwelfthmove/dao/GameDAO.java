// /backend/src/main/java/com/thetwelfthmove/dao/GameDAO.java
package com.thetwelfthmove.dao;

import com.thetwelfthmove.models.Game;
import com.thetwelfthmove.models.Move;
import com.thetwelfthmove.models.ChessBoard;
import com.thetwelfthmove.utils.GameCodeGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private final Connection conn;

    public GameDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    // Create a local 2-player game (same device, no online features)
    public Game createLocalGame(int playerId) {
        try {
            ChessBoard board = new ChessBoard();
            String boardJson = board.toJson();
            
            // No game code for local games, status is 'ongoing' immediately
            String sql = "INSERT INTO games (player1_id, player2_id, board_state, current_turn, status, game_code) VALUES (?, ?, ?, 'white', 'ongoing', NULL)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId); // Same player for both (local mode indicator)
            stmt.setString(3, boardJson);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int gameId = keys.getInt(1);
                    return getGameById(gameId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Create a new game with unique code
    public Game createGame(int player1Id, Integer player2Id) {
        try {
            ChessBoard board = new ChessBoard();
            String boardJson = board.toJson();
            
            // Generate unique game code
            String gameCode = generateUniqueGameCode();
            
            // Status is 'waiting' if no player2, 'ongoing' if player2 exists
            String status = player2Id == null ? "waiting" : "ongoing";
            
            String sql = "INSERT INTO games (player1_id, player2_id, board_state, current_turn, status, game_code) VALUES (?, ?, ?, 'white', ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, player1Id);
            if (player2Id != null) {
                stmt.setInt(2, player2Id);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, boardJson);
            stmt.setString(4, status);
            stmt.setString(5, gameCode);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int gameId = keys.getInt(1);
                    return getGameById(gameId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Generate unique game code
    private String generateUniqueGameCode() throws SQLException {
        String code;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            code = GameCodeGenerator.generateCode();
            attempts++;
            
            if (attempts >= maxAttempts) {
                throw new SQLException("Failed to generate unique game code after " + maxAttempts + " attempts");
            }
        } while (isGameCodeTaken(code));
        
        return code;
    }

    // Check if game code already exists
    private boolean isGameCodeTaken(String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM games WHERE game_code = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, code);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    // Get game by ID
    public Game getGameById(int gameId) {
        try {
            String sql = "SELECT * FROM games WHERE game_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGame(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get game by code
    public Game getGameByCode(String gameCode) {
        try {
            String sql = "SELECT * FROM games WHERE game_code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, gameCode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGame(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Join game by code
    public boolean joinGame(String gameCode, int player2Id) {
        try {
            // Check if game exists and is waiting
            Game game = getGameByCode(gameCode);
            if (game == null) return false;
            if (!game.getStatus().equals("waiting")) return false;
            if (game.getPlayer1Id() == player2Id) return false; // Can't join own game
            
            // Update game with player2 and change status to ongoing
            String sql = "UPDATE games SET player2_id = ?, status = 'ongoing' WHERE game_code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, player2Id);
            stmt.setString(2, gameCode);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update game state
    public boolean updateGameState(int gameId, String boardState, String currentTurn) {
        try {
            String sql = "UPDATE games SET board_state = ?, current_turn = ?, last_move_time = NOW() WHERE game_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, boardState);
            stmt.setString(2, currentTurn);
            stmt.setInt(3, gameId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // End game
    public boolean endGame(int gameId, String result, Integer winnerId) {
        try {
            String sql = "UPDATE games SET status = 'completed', result = ?, winner_id = ?, end_time = NOW(), duration = TIMESTAMPDIFF(SECOND, start_time, NOW()) WHERE game_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, result);
            if (winnerId != null) {
                stmt.setInt(2, winnerId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, gameId);
            
            boolean updated = stmt.executeUpdate() > 0;
            
            // Update player stats if there's a winner
            if (updated && winnerId != null) {
                updatePlayerStats(gameId, winnerId);
            }
            
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update player statistics
    private void updatePlayerStats(int gameId, int winnerId) {
        try {
            Game game = getGameById(gameId);
            if (game == null) return;
            
            // Increment games_played for both players
            String sql1 = "UPDATE players SET games_played = games_played + 1 WHERE id = ? OR id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(sql1);
            stmt1.setInt(1, game.getPlayer1Id());
            if (game.getPlayer2Id() != null) {
                stmt1.setInt(2, game.getPlayer2Id());
            } else {
                stmt1.setInt(2, game.getPlayer1Id()); // Just use same ID if no player2
            }
            stmt1.executeUpdate();
            
            // Increment games_won for winner
            String sql2 = "UPDATE players SET games_won = games_won + 1 WHERE id = ?";
            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.setInt(1, winnerId);
            stmt2.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save a move
    public boolean saveMove(Move move) {
        try {
            String sql = "INSERT INTO moves (game_id, player_id, move_number, from_square, to_square, piece_type, piece_color, captured_piece, is_check, is_checkmate, move_notation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, move.getGameId());
            stmt.setInt(2, move.getPlayerId());
            stmt.setInt(3, move.getMoveNumber());
            stmt.setString(4, move.getFromSquare());
            stmt.setString(5, move.getToSquare());
            stmt.setString(6, move.getPieceType());
            stmt.setString(7, move.getPieceColor());
            stmt.setString(8, move.getCapturedPiece());
            stmt.setBoolean(9, move.isCheck());
            stmt.setBoolean(10, move.isCheckmate());
            stmt.setString(11, move.getMoveNotation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all moves for a game
    public List<Move> getGameMoves(int gameId) {
        List<Move> moves = new ArrayList<>();
        try {
            String sql = "SELECT * FROM moves WHERE game_id = ? ORDER BY move_number ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                moves.add(mapResultSetToMove(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moves;
    }

    // Get ongoing games for a player
    public List<Game> getPlayerGames(int playerId, String status) {
        List<Game> games = new ArrayList<>();
        try {
            String sql = "SELECT * FROM games WHERE (player1_id = ? OR player2_id = ?) AND status = ? ORDER BY start_time DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId);
            stmt.setString(3, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                games.add(mapResultSetToGame(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    // Get recent completed games for a player (for dashboard)
    public List<Game> getRecentGames(int playerId, int limit) {
        List<Game> games = new ArrayList<>();
        try {
            String sql = "SELECT * FROM games WHERE (player1_id = ? OR player2_id = ?) AND status = 'completed' ORDER BY end_time DESC LIMIT ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId);
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                games.add(mapResultSetToGame(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    // Helper: Map ResultSet to Game
    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setGameId(rs.getInt("game_id"));
        game.setPlayer1Id(rs.getInt("player1_id"));
        
        int player2Id = rs.getInt("player2_id");
        game.setPlayer2Id(rs.wasNull() ? null : player2Id);
        
        game.setGameCode(rs.getString("game_code"));
        game.setCurrentTurn(rs.getString("current_turn"));
        game.setBoardState(rs.getString("board_state"));
        game.setStartTime(rs.getTimestamp("start_time"));
        game.setEndTime(rs.getTimestamp("end_time"));
        
        int duration = rs.getInt("duration");
        game.setDuration(rs.wasNull() ? null : duration);
        
        int winnerId = rs.getInt("winner_id");
        game.setWinnerId(rs.wasNull() ? null : winnerId);
        
        game.setStatus(rs.getString("status"));
        game.setResult(rs.getString("result"));
        game.setLastMoveTime(rs.getTimestamp("last_move_time"));
        
        return game;
    }

    // Helper: Map ResultSet to Move
    private Move mapResultSetToMove(ResultSet rs) throws SQLException {
        Move move = new Move();
        move.setMoveId(rs.getInt("move_id"));
        move.setGameId(rs.getInt("game_id"));
        move.setPlayerId(rs.getInt("player_id"));
        move.setMoveNumber(rs.getInt("move_number"));
        move.setFromSquare(rs.getString("from_square"));
        move.setToSquare(rs.getString("to_square"));
        move.setPieceType(rs.getString("piece_type"));
        move.setPieceColor(rs.getString("piece_color"));
        move.setCapturedPiece(rs.getString("captured_piece"));
        move.setCheck(rs.getBoolean("is_check"));
        move.setCheckmate(rs.getBoolean("is_checkmate"));
        move.setMoveNotation(rs.getString("move_notation"));
        move.setTimestamp(rs.getTimestamp("timestamp"));
        
        return move;
    }
}