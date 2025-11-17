package com.thetwelfthmove.model;

import java.sql.Timestamp;

public class Move {
    private int id;
    private int gameId;
    private int playerId;
    private int moveNumber;
    private String fromPosition;
    private String toPosition;
    private String pieceType;
    private Timestamp createdAt;

    public Move() {}

    public Move(int id, int gameId, int playerId, int moveNumber, String fromPosition, String toPosition, String pieceType, Timestamp createdAt) {
        this.id = id;
        this.gameId = gameId;
        this.playerId = playerId;
        this.moveNumber = moveNumber;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.pieceType = pieceType;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }
    public String getFromPosition() { return fromPosition; }
    public void setFromPosition(String fromPosition) { this.fromPosition = fromPosition; }
    public String getToPosition() { return toPosition; }
    public void setToPosition(String toPosition) { this.toPosition = toPosition; }
    public String getPieceType() { return pieceType; }
    public void setPieceType(String pieceType) { this.pieceType = pieceType; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
