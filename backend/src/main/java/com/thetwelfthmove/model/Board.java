package com.thetwelfthmove.model;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private Map<String, Piece> board;

    public Board() {
        board = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialize white pieces
        board.put("A1", new Piece("Rook", "White"));
        board.put("B1", new Piece("Knight", "White"));
        board.put("C1", new Piece("Bishop", "White"));
        board.put("D1", new Piece("Queen", "White"));
        board.put("E1", new Piece("King", "White"));
        board.put("F1", new Piece("Bishop", "White"));
        board.put("G1", new Piece("Knight", "White"));
        board.put("H1", new Piece("Rook", "White"));
        for (char c = 'A'; c <= 'H'; c++) {
            board.put(c + "2", new Piece("Pawn", "White"));
        }

        // Initialize black pieces
        board.put("A8", new Piece("Rook", "Black"));
        board.put("B8", new Piece("Knight", "Black"));
        board.put("C8", new Piece("Bishop", "Black"));
        board.put("D8", new Piece("Queen", "Black"));
        board.put("E8", new Piece("King", "Black"));
        board.put("F8", new Piece("Bishop", "Black"));
        board.put("G8", new Piece("Knight", "Black"));
        board.put("H8", new Piece("Rook", "Black"));
        for (char c = 'A'; c <= 'H'; c++) {
            board.put(c + "7", new Piece("Pawn", "Black"));
        }
    }

    public Piece getPiece(String position) {
        return board.get(position);
    }

    public void setPiece(String position, Piece piece) {
        board.put(position, piece);
    }

    public void removePiece(String position) {
        board.remove(position);
    }

    public boolean isValidPosition(String position) {
        if (position.length() != 2) return false;
        char file = position.charAt(0);
        char rank = position.charAt(1);
        return file >= 'A' && file <= 'H' && rank >= '1' && rank <= '8';
    }
}
