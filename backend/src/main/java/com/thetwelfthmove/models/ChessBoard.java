// /backend/src/main/java/com/thetwelfthmove/models/ChessBoard.java
package com.thetwelfthmove.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class ChessBoard {
    private ChessPiece[][] board; // 8x8 board
    private static final Gson gson = new Gson();

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        initializeBoard();
    }

    // Initialize standard chess starting position
    private void initializeBoard() {
        // Place black pieces (row 0 and 1)
        board[0][0] = new ChessPiece("rook", "black");
        board[0][1] = new ChessPiece("knight", "black");
        board[0][2] = new ChessPiece("bishop", "black");
        board[0][3] = new ChessPiece("queen", "black");
        board[0][4] = new ChessPiece("king", "black");
        board[0][5] = new ChessPiece("bishop", "black");
        board[0][6] = new ChessPiece("knight", "black");
        board[0][7] = new ChessPiece("rook", "black");
        
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece("pawn", "black");
        }

        // Place white pieces (row 6 and 7)
        for (int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece("pawn", "white");
        }
        
        board[7][0] = new ChessPiece("rook", "white");
        board[7][1] = new ChessPiece("knight", "white");
        board[7][2] = new ChessPiece("bishop", "white");
        board[7][3] = new ChessPiece("queen", "white");
        board[7][4] = new ChessPiece("king", "white");
        board[7][5] = new ChessPiece("bishop", "white");
        board[7][6] = new ChessPiece("knight", "white");
        board[7][7] = new ChessPiece("rook", "white");
    }

    // Convert board to JSON string for storage
    public String toJson() {
        return gson.toJson(board);
    }

    // Load board from JSON string
    public static ChessBoard fromJson(String json) {
        ChessBoard chessBoard = new ChessBoard();
        Type type = new TypeToken<ChessPiece[][]>(){}.getType();
        chessBoard.board = gson.fromJson(json, type);
        return chessBoard;
    }

    // Get piece at position
    public ChessPiece getPiece(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return null;
        return board[row][col];
    }

    // Set piece at position
    public void setPiece(int row, int col, ChessPiece piece) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            board[row][col] = piece;
        }
    }

    // Convert algebraic notation to array indices (e.g., "e2" -> [6, 4])
    public static int[] notationToIndices(String notation) {
        if (notation == null || notation.length() != 2) return null;
        int col = notation.charAt(0) - 'a'; // 'a' = 0, 'b' = 1, etc.
        int row = 8 - (notation.charAt(1) - '0'); // '8' = 0, '7' = 1, etc.
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return null;
        return new int[]{row, col};
    }

    // Convert array indices to algebraic notation (e.g., [6, 4] -> "e2")
    public static String indicesToNotation(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return null;
        char file = (char) ('a' + col);
        char rank = (char) ('8' - row);
        return "" + file + rank;
    }

    // Validate if a move is legal
    public boolean isValidMove(String from, String to, String playerColor) {
        int[] fromPos = notationToIndices(from);
        int[] toPos = notationToIndices(to);
        
        if (fromPos == null || toPos == null) return false;
        
        ChessPiece piece = getPiece(fromPos[0], fromPos[1]);
        ChessPiece targetPiece = getPiece(toPos[0], toPos[1]);
        
        // Check if there's a piece at the source
        if (piece == null) return false;
        
        // Check if it's the player's piece
        if (!piece.getColor().equals(playerColor)) return false;
        
        // Check if target has own piece
        if (targetPiece != null && targetPiece.getColor().equals(playerColor)) return false;
        
        // Validate move based on piece type
        boolean validMove = false;
        switch (piece.getType()) {
            case "pawn":
                validMove = isValidPawnMove(fromPos, toPos, piece.getColor(), targetPiece);
                break;
            case "knight":
                validMove = isValidKnightMove(fromPos, toPos);
                break;
            case "bishop":
                validMove = isValidBishopMove(fromPos, toPos);
                break;
            case "rook":
                validMove = isValidRookMove(fromPos, toPos);
                break;
            case "queen":
                validMove = isValidQueenMove(fromPos, toPos);
                break;
            case "king":
                validMove = isValidKingMove(fromPos, toPos);
                break;
        }
        
        if (!validMove) return false;
        
        // Check if move would put own king in check (simulate move)
        return !wouldBeInCheck(fromPos, toPos, playerColor);
    }

    // Pawn movement validation
    private boolean isValidPawnMove(int[] from, int[] to, String color, ChessPiece target) {
        int direction = color.equals("white") ? -1 : 1;
        int startRow = color.equals("white") ? 6 : 1;
        
        int rowDiff = to[0] - from[0];
        int colDiff = Math.abs(to[1] - from[1]);
        
        // Forward move (no capture)
        if (colDiff == 0 && target == null) {
            if (rowDiff == direction) return true; // One square forward
            if (rowDiff == 2 * direction && from[0] == startRow) {
                // Two squares forward from start - check if path is clear
                int middleRow = from[0] + direction;
                return getPiece(middleRow, from[1]) == null;
            }
        }
        
        // Diagonal capture
        if (colDiff == 1 && rowDiff == direction && target != null) {
            return true;
        }
        
        return false;
    }

    // Knight movement validation
    private boolean isValidKnightMove(int[] from, int[] to) {
        int rowDiff = Math.abs(to[0] - from[0]);
        int colDiff = Math.abs(to[1] - from[1]);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    // Bishop movement validation
    private boolean isValidBishopMove(int[] from, int[] to) {
        int rowDiff = Math.abs(to[0] - from[0]);
        int colDiff = Math.abs(to[1] - from[1]);
        
        if (rowDiff != colDiff) return false; // Must move diagonally
        
        return isPathClear(from, to);
    }

    // Rook movement validation
    private boolean isValidRookMove(int[] from, int[] to) {
        if (from[0] != to[0] && from[1] != to[1]) return false; // Must move straight
        
        return isPathClear(from, to);
    }

    // Queen movement validation (combination of rook and bishop)
    private boolean isValidQueenMove(int[] from, int[] to) {
        return isValidRookMove(from, to) || isValidBishopMove(from, to);
    }

    // King movement validation
    private boolean isValidKingMove(int[] from, int[] to) {
        int rowDiff = Math.abs(to[0] - from[0]);
        int colDiff = Math.abs(to[1] - from[1]);
        return rowDiff <= 1 && colDiff <= 1;
    }

    // Check if path between two squares is clear (for rook, bishop, queen)
    private boolean isPathClear(int[] from, int[] to) {
        int rowDir = Integer.compare(to[0], from[0]);
        int colDir = Integer.compare(to[1], from[1]);
        
        int currentRow = from[0] + rowDir;
        int currentCol = from[1] + colDir;
        
        while (currentRow != to[0] || currentCol != to[1]) {
            if (getPiece(currentRow, currentCol) != null) return false;
            currentRow += rowDir;
            currentCol += colDir;
        }
        
        return true;
    }

    // Check if a move would result in check for the moving player
    private boolean wouldBeInCheck(int[] from, int[] to, String playerColor) {
        // Simulate the move
        ChessPiece movingPiece = getPiece(from[0], from[1]);
        ChessPiece capturedPiece = getPiece(to[0], to[1]);
        
        board[to[0]][to[1]] = movingPiece;
        board[from[0]][from[1]] = null;
        
        boolean inCheck = isKingInCheck(playerColor);
        
        // Undo the move
        board[from[0]][from[1]] = movingPiece;
        board[to[0]][to[1]] = capturedPiece;
        
        return inCheck;
    }

    // Check if a player's king is in check
    public boolean isKingInCheck(String playerColor) {
        // Find the king
        int[] kingPos = findKing(playerColor);
        if (kingPos == null) return false;
        
        // Check if any opponent piece can attack the king
        String opponentColor = playerColor.equals("white") ? "black" : "white";
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = getPiece(row, col);
                if (piece != null && piece.getColor().equals(opponentColor)) {
                    if (canPieceAttack(new int[]{row, col}, kingPos)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    // Find king position
    private int[] findKing(String color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = getPiece(row, col);
                if (piece != null && piece.getType().equals("king") && piece.getColor().equals(color)) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    // Check if a piece can attack a target square (ignoring check rules)
    private boolean canPieceAttack(int[] from, int[] to) {
        ChessPiece piece = getPiece(from[0], from[1]);
        if (piece == null) return false;
        
        switch (piece.getType()) {
            case "pawn":
                int direction = piece.getColor().equals("white") ? -1 : 1;
                int rowDiff = to[0] - from[0];
                int colDiff = Math.abs(to[1] - from[1]);
                return rowDiff == direction && colDiff == 1;
            case "knight":
                return isValidKnightMove(from, to);
            case "bishop":
                return isValidBishopMove(from, to);
            case "rook":
                return isValidRookMove(from, to);
            case "queen":
                return isValidQueenMove(from, to);
            case "king":
                return isValidKingMove(from, to);
        }
        
        return false;
    }

    // Check if it's checkmate
    public boolean isCheckmate(String playerColor) {
        if (!isKingInCheck(playerColor)) return false;
        
        // Check if any move can get out of check
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                ChessPiece piece = getPiece(fromRow, fromCol);
                if (piece != null && piece.getColor().equals(playerColor)) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            String from = indicesToNotation(fromRow, fromCol);
                            String to = indicesToNotation(toRow, toCol);
                            if (isValidMove(from, to, playerColor)) {
                                return false; // Found a valid move
                            }
                        }
                    }
                }
            }
        }
        
        return true; // No valid moves, it's checkmate
    }

    // Execute a move (assumes move is valid)
    public ChessPiece executeMove(String from, String to) {
        int[] fromPos = notationToIndices(from);
        int[] toPos = notationToIndices(to);
        
        ChessPiece piece = getPiece(fromPos[0], fromPos[1]);
        ChessPiece capturedPiece = getPiece(toPos[0], toPos[1]);
        
        board[toPos[0]][toPos[1]] = piece;
        board[fromPos[0]][fromPos[1]] = null;
        
        return capturedPiece;
    }

    public ChessPiece[][] getBoard() {
        return board;
    }
}