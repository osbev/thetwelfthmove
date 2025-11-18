// /backend/src/main/java/com/thetwelfthmove/models/ChessPiece.java
package com.thetwelfthmove.models;

public class ChessPiece {
    private String type;  // "pawn", "knight", "bishop", "rook", "queen", "king"
    private String color; // "white" or "black"

    public ChessPiece(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color + "_" + type;
    }
}