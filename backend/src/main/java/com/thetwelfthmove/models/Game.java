// /backend/src/main/java/com/thetwelfthmove/models/Game.java
package com.thetwelfthmove.models;

import java.sql.Timestamp;

public class Game {
    private int gameId;
    private int player1Id;
    private Integer player2Id; // Nullable
    private String currentTurn; // "white" or "black"
    private String boardState; // JSON string
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer duration; // in seconds
    private Integer winnerId; // Nullable
    private String status; // "ongoing", "completed", "draw", "abandoned"
    private String result; // "white_wins", "black_wins", "draw", "checkmate", "stalemate"

    // Constructors
    public Game() {}

    public Game(int player1Id, Integer player2Id, String boardState) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.boardState = boardState;
        this.currentTurn = "white";
        this.status = "ongoing";
    }

    // Getters and Setters
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public Integer getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Integer player2Id) {
        this.player2Id = player2Id;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}