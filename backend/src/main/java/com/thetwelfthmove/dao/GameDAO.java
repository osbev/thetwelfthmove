package com.thetwelfthmove.dao;

import com.thetwelfthmove.model.Game;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public static List<Game> getAllGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Game g = new Game(
                        rs.getInt("id"),
                        rs.getInt("player1_id"),
                        (Integer) rs.getObject("player2_id"),
                        (Integer) rs.getObject("winner_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                games.add(g);
            }
        }
        return games;
    }

    public static void createGame(int player1Id, Integer player2Id) throws SQLException {
        String sql = "INSERT INTO games (player1_id, player2_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, player1Id);
            if (player2Id != null) {
                ps.setInt(2, player2Id);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }
}
