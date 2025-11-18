// /backend/src/main/java/com/thetwelfthmove/models/Move.java
package com.thetwelfthmove.models;

import java.sql.Timestamp;

public class Move {
    private int moveId;
    private int gameId;
    private int playerId;
    private int moveNumber;
    private String fromSquare; // e.g., "e2"
    private String toSquare;   // e.g., "e4"
    private String pieceType;  // "pawn", "knight", "bishop", "rook", "queen", "king"
    private String pieceColor; // "white" or "black"
    private String capturedPiece; // null if no capture
    private boolean isCheck;
    private boolean isCheckmate;
    private String moveNotation; // algebraic notation
    private Timestamp timestamp;

    // Constructors
    public Move() {}

    public Move(int gameId, int playerId, int moveNumber, String fromSquare, String toSquare, 
                String pieceType, String pieceColor) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.moveNumber = moveNumber;
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
    }

    // Getters and Setters
    public int getMoveId() {
        return moveId;
    }

    public void setMoveId(int moveId) {
        this.moveId = moveId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getFromSquare() {
        return fromSquare;
    }

    public void setFromSquare(String fromSquare) {
        this.fromSquare = fromSquare;
    }

    public String getToSquare() {
        return toSquare;
    }

    public void setToSquare(String toSquare) {
        this.toSquare = toSquare;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public String getPieceColor() {
        return pieceColor;
    }

    public void setPieceColor(String pieceColor) {
        this.pieceColor = pieceColor;
    }

    public String getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(String capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }

    public String getMoveNotation() {
        return moveNotation;
    }

    public void setMoveNotation(String moveNotation) {
        this.moveNotation = moveNotation;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}