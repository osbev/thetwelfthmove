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
    publi
