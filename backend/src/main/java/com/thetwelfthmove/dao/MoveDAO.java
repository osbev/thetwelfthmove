package com.thetwelfthmove.dao;

import com.thetwelfthmove.model.Move;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {

    public static List<Move> getMovesByGame(int gameId) throws SQLException {
        List<Move> moves = new ArrayList<>();
        String sql = "SELECT * FROM moves WHERE game_id = ? ORDER BY move_number ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Move m = new Move(
                        rs.getInt("id"),
                        rs.getInt("game_id"),
                        rs.getInt("player_id"),
                        rs.getInt("move_number"),
                        rs.getString("from_position"),
                        rs.getString("to_position"),
                        rs.getString("piece_type"),
                        rs.getTimestamp("created_at")
                );
                moves.add(m);
            }
        }

        return moves;
    }

    public static void addMove(Move move) throws SQLException {
        String sql = "INSERT INTO moves (game_id, player_id, move_number, from_position, to_position, piece_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, move.getGameId());
            ps.setInt(2, move.getPlayerId());
            ps.setInt(3, move.getMoveNumber());
            ps.setString(4, move.getFromPosition());
            ps.setString(5, move.getToPosition());
            ps.setString(6, move.getPieceType());
            ps.executeUpdate();
        }
    }
}
