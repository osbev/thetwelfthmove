package com.thetwelfthmove.dao;

import com.thetwelfthmove.models.Player;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {

    private final Connection conn;

    public PlayerDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    // Find player by username
    public Player findByUsername(String username) {
        try {
            String sql = "SELECT * FROM players WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                player.setGamesPlayed(rs.getInt("games_played"));
                player.setGamesWon(rs.getInt("games_won"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding player: " + e.getMessage(), e);
        }
        return null;
    }

    // Create a new player (signup)
    public boolean createPlayer(String username, String plainPassword) {
        if (findByUsername(username) != null) {
            return false; // username already exists
        }

        try {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
            String sql = "INSERT INTO players (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add this method to your existing PlayerDAO.java

    // Find player by ID
    public Player findById(int playerId) {
        try {
            String sql = "SELECT * FROM players WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                player.setGamesPlayed(rs.getInt("games_played"));
                player.setGamesWon(rs.getInt("games_won"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding player: " + e.getMessage(), e);
        }
        return null;
    }
}


