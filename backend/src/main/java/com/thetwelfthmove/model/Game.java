package com.thetwelfthmove.model;

import java.sql.Timestamp;

public class Game {
    private int id;
    private int player1Id;
    private Integer player2Id; // nullable
    private Integer winnerId;  // nullable
    private String status;     // ONGOING, FINISHED, DRAW
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Game() {}

    public Game(int id, int player1Id, Integer player2Id, Integer winnerId, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(int player1Id) { this.player1Id = player1Id; }
    public Integer getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(Integer player2Id) { this.player2Id = player2Id; }
    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
