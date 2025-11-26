// /backend/src/main/java/com/thetwelfthmove/models/ChessBoard.java
package com.thetwelfthmove.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class ChessBoard {
    private ChessPiece[][] board; // 8x8 board
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookKingsideMoved = false;
    private boolean whiteRookQueensideMoved = false;
    private boolean blackRookKingsideMoved = false;
    private boolean blackRookQueensideMoved = false;
    private String enPassantTarget = null; // Square where en passant is possible (e.g., "e3")
    
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
        Map<String, Object> state = new HashMap<>();
        state.put("board", board);
        state.put("whiteKingMoved", whiteKingMoved);
        state.put("blackKingMoved", blackKingMoved);
        state.put("whiteRookKingsideMoved", whiteRookKingsideMoved);
        state.put("whiteRookQueensideMoved", whiteRookQueensideMoved);
        state.put("blackRookKingsideMoved", blackRookKingsideMoved);
        state.put("blackRookQueensideMoved", blackRookQueensideMoved);
        state.put("enPassantTarget", enPassantTarget);
        return gson.toJson(state);
    }

    // Load board from JSON string
    public static ChessBoard fromJson(String json) {
        ChessBoard chessBoard = new ChessBoard();
        try {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> state = gson.fromJson(json, type);
            
            // Load board
            String boardJson = gson.toJson(state.get("board"));
            Type boardType = new TypeToken<ChessPiece[][]>(){}.getType();
            chessBoard.board = gson.fromJson(boardJson, boardType);
            
            // Load castling rights
            chessBoard.whiteKingMoved = state.get("whiteKingMoved") != null ? (Boolean) state.get("whiteKingMoved") : false;
            chessBoard.blackKingMoved = state.get("blackKingMoved") != null ? (Boolean) state.get("blackKingMoved") : false;
            chessBoard.whiteRookKingsideMoved = state.get("whiteRookKingsideMoved") != null ? (Boolean) state.get("whiteRookKingsideMoved") : false;
            chessBoard.whiteRookQueensideMoved = state.get("whiteRookQueensideMoved") != null ? (Boolean) state.get("whiteRookQueensideMoved") : false;
            chessBoard.blackRookKingsideMoved = state.get("blackRookKingsideMoved") != null ? (Boolean) state.get("blackRookKingsideMoved") : false;
            chessBoard.blackRookQueensideMoved = state.get("blackRookQueensideMoved") != null ? (Boolean) state.get("blackRookQueensideMoved") : false;
            
            // Load en passant target
            chessBoard.enPassantTarget = (String) state.get("enPassantTarget");
        } catch (Exception e) {
            // Fallback for old format (just board array)
            Type boardType = new TypeToken<ChessPiece[][]>(){}.getType();
            chessBoard.board = gson.fromJson(json, boardType);
        }
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

    // Validate if a move is legal (NO LONGER CHECKS IF MOVE PUTS OWN KING IN CHECK)
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
        
        // Check if target has own piece (can't capture own pieces)
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
                validMove = isValidKingMove(fromPos, toPos, piece.getColor());
                break;
        }
        
        return validMove;
    }

    // Pawn movement validation (includes en passant)
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
        if (colDiff == 1 && rowDiff == direction) {
            // Regular capture
            if (target != null) return true;
            
            // En passant
            String toSquare = indicesToNotation(to[0], to[1]);
            if (toSquare.equals(enPassantTarget)) {
                return true;
            }
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

    // King movement validation (includes castling)
    private boolean isValidKingMove(int[] from, int[] to, String color) {
        int rowDiff = Math.abs(to[0] - from[0]);
        int colDiff = Math.abs(to[1] - from[1]);
        
        // Normal king move (one square)
        if (rowDiff <= 1 && colDiff <= 1) return true;
        
        // Castling
        if (rowDiff == 0 && colDiff == 2) {
            return canCastle(from, to, color);
        }
        
        return false;
    }

    // Check if castling is valid
    private boolean canCastle(int[] from, int[] to, String color) {
        // King must not have moved
        if (color.equals("white") && whiteKingMoved) return false;
        if (color.equals("black") && blackKingMoved) return false;
        
        // Determine kingside or queenside
        boolean kingside = to[1] > from[1];
        int rookCol = kingside ? 7 : 0;
        int row = from[0];
        
        // Rook must not have moved
        if (color.equals("white")) {
            if (kingside && whiteRookKingsideMoved) return false;
            if (!kingside && whiteRookQueensideMoved) return false;
        } else {
            if (kingside && blackRookKingsideMoved) return false;
            if (!kingside && blackRookQueensideMoved) return false;
        }
        
        // Check if rook exists
        ChessPiece rook = getPiece(row, rookCol);
        if (rook == null || !rook.getType().equals("rook")) return false;
        
        // Path must be clear between king and rook
        int start = Math.min(from[1], rookCol) + 1;
        int end = Math.max(from[1], rookCol);
        for (int col = start; col < end; col++) {
            if (getPiece(row, col) != null) return false;
        }
        
        // King must not be in check
        if (isKingInCheck(color)) return false;
        
        // King must not move through check
        int direction = kingside ? 1 : -1;
        for (int i = 0; i <= 2; i++) {
            int col = from[1] + (i * direction);
            if (isSquareUnderAttack(row, col, color)) return false;
        }
        
        return true;
    }

    // Check if a square is under attack by opponent
    private boolean isSquareUnderAttack(int row, int col, String playerColor) {
        String opponentColor = playerColor.equals("white") ? "black" : "white";
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = getPiece(r, c);
                if (piece != null && piece.getColor().equals(opponentColor)) {
                    if (canPieceAttack(new int[]{r, c}, new int[]{row, col})) {
                        return true;
                    }
                }
            }
        }
        
        return false;
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
                int r = Math.abs(to[0] - from[0]);
                int c = Math.abs(to[1] - from[1]);
                return r <= 1 && c <= 1;
        }
        
        return false;
    }

    // Check if it's checkmate (player has NO legal moves that get out of check)
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
                            
                            // Check if move is valid (basic rules)
                            if (isValidMove(from, to, playerColor)) {
                                // Simulate move and check if still in check
                                ChessPiece movingPiece = getPiece(fromRow, fromCol);
                                ChessPiece capturedPiece = getPiece(toRow, toCol);
                                
                                board[toRow][toCol] = movingPiece;
                                board[fromRow][fromCol] = null;
                                
                                boolean stillInCheck = isKingInCheck(playerColor);
                                
                                // Undo move
                                board[fromRow][fromCol] = movingPiece;
                                board[toRow][toCol] = capturedPiece;
                                
                                if (!stillInCheck) {
                                    return false; // Found a move that gets out of check
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return true; // No valid moves that get out of check - checkmate
    }

    // Execute a move (assumes move is valid)
    public ChessPiece executeMove(String from, String to) {
        int[] fromPos = notationToIndices(from);
        int[] toPos = notationToIndices(to);
        
        ChessPiece piece = getPiece(fromPos[0], fromPos[1]);
        ChessPiece capturedPiece = getPiece(toPos[0], toPos[1]);
        
        // Handle en passant capture
        if (piece.getType().equals("pawn") && to.equals(enPassantTarget)) {
            int captureRow = piece.getColor().equals("white") ? toPos[0] + 1 : toPos[0] - 1;
            capturedPiece = getPiece(captureRow, toPos[1]);
            board[captureRow][toPos[1]] = null;
        }
        
        // Handle castling
        if (piece.getType().equals("king") && Math.abs(toPos[1] - fromPos[1]) == 2) {
            // Move rook
            boolean kingside = toPos[1] > fromPos[1];
            int rookFromCol = kingside ? 7 : 0;
            int rookToCol = kingside ? toPos[1] - 1 : toPos[1] + 1;
            
            ChessPiece rook = getPiece(fromPos[0], rookFromCol);
            board[fromPos[0]][rookToCol] = rook;
            board[fromPos[0]][rookFromCol] = null;
        }
        
        // Move piece
        board[toPos[0]][toPos[1]] = piece;
        board[fromPos[0]][fromPos[1]] = null;
        
        // Update castling rights
        if (piece.getType().equals("king")) {
            if (piece.getColor().equals("white")) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }
        
        if (piece.getType().equals("rook")) {
            if (piece.getColor().equals("white")) {
                if (fromPos[1] == 7) whiteRookKingsideMoved = true;
                if (fromPos[1] == 0) whiteRookQueensideMoved = true;
            } else {
                if (fromPos[1] == 7) blackRookKingsideMoved = true;
                if (fromPos[1] == 0) blackRookQueensideMoved = true;
            }
        }
        
        // Set en passant target if pawn moved two squares
        enPassantTarget = null;
        if (piece.getType().equals("pawn") && Math.abs(toPos[0] - fromPos[0]) == 2) {
            int middleRow = (fromPos[0] + toPos[0]) / 2;
            enPassantTarget = indicesToNotation(middleRow, fromPos[1]);
        }
        
        return capturedPiece;
    }

    public ChessPiece[][] getBoard() {
        return board;
    }
    
    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Simulates a move (including special moves like castling and en passant) and checks if it causes playerColor's king to be in check after the move.
     * Does not modify state permanently.
     * @param from source square in notation (e.g. "e2")
     * @param to target square in notation (e.g. "e4")
     * @param playerColor "white" or "black"
     * @return true if the move would leave player's king in check, false otherwise
     */
    public boolean wouldMoveCauseCheck(String from, String to, String playerColor) {
        int[] fromPos = notationToIndices(from);
        int[] toPos = notationToIndices(to);
        
        ChessPiece movingPiece = getPiece(fromPos[0], fromPos[1]);
        ChessPiece targetPiece = getPiece(toPos[0], toPos[1]);
        ChessPiece capturedPiece = targetPiece;
        
        if (movingPiece == null) return true; // No piece to move - treat as invalid
        
        // Backup state variables for castling and en passant flags to revert later
        boolean oldWhiteKingMoved = whiteKingMoved;
        boolean oldBlackKingMoved = blackKingMoved;
        boolean oldWhiteRookKingsideMoved = whiteRookKingsideMoved;
        boolean oldWhiteRookQueensideMoved = whiteRookQueensideMoved;
        boolean oldBlackRookKingsideMoved = blackRookKingsideMoved;
        boolean oldBlackRookQueensideMoved = blackRookQueensideMoved;
        String oldEnPassantTarget = enPassantTarget;
        
        // Execute the move temporarily on the board including special moves
        // Handle en passant capture
        if (movingPiece.getType().equals("pawn") && to.equals(enPassantTarget)) {
            int captureRow = movingPiece.getColor().equals("white") ? toPos[0] + 1 : toPos[0] - 1;
            capturedPiece = getPiece(captureRow, toPos[1]);
            board[captureRow][toPos[1]] = null;
        }
        
        // Handle castling rook move when king moves two squares
        if (movingPiece.getType().equals("king") && Math.abs(toPos[1] - fromPos[1]) == 2) {
            boolean kingside = toPos[1] > fromPos[1];
            int rookFromCol = kingside ? 7 : 0;
            int rookToCol = kingside ? toPos[1] - 1 : toPos[1] + 1;
            
            ChessPiece rook = getPiece(fromPos[0], rookFromCol);
            board[fromPos[0]][rookToCol] = rook;
            board[fromPos[0]][rookFromCol] = null;
        }
        
        // Move piece
        board[toPos[0]][toPos[1]] = movingPiece;
        board[fromPos[0]][fromPos[1]] = null;
        
        // Update castling rights if king or rook moved (simulate update)
        if (movingPiece.getType().equals("king")) {
            if (movingPiece.getColor().equals("white")) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }
        
        if (movingPiece.getType().equals("rook")) {
            if (movingPiece.getColor().equals("white")) {
                if (fromPos[1] == 7) whiteRookKingsideMoved = true;
                if (fromPos[1] == 0) whiteRookQueensideMoved = true;
            } else {
                if (fromPos[1] == 7) blackRookKingsideMoved = true;
                if (fromPos[1] == 0) blackRookQueensideMoved = true;
            }
        }
        
        // Clear en passant target to simulate move
        enPassantTarget = null;
        
        // Check if player's king is in check after move
        boolean isInCheck = isKingInCheck(playerColor);
        
        // Undo move - revert board and flags
        board[fromPos[0]][fromPos[1]] = movingPiece;
        board[toPos[0]][toPos[1]] = targetPiece;
        
        // Revert special en passant capture pawn
        if (movingPiece.getType().equals("pawn") && to.equals(oldEnPassantTarget)) {
            int captureRow = movingPiece.getColor().equals("white") ? toPos[0] + 1 : toPos[0] - 1;
            board[captureRow][toPos[1]] = capturedPiece;
        }
        
        // Revert castling rook move
        if (movingPiece.getType().equals("king") && Math.abs(toPos[1] - fromPos[1]) == 2) {
            boolean kingside = toPos[1] > fromPos[1];
            int rookFromCol = kingside ? 7 : 0;
            int rookToCol = kingside ? toPos[1] - 1 : toPos[1] + 1;
            
            ChessPiece rook = getPiece(fromPos[0], rookToCol);
            board[fromPos[0]][rookFromCol] = rook;
            board[fromPos[0]][rookToCol] = null;
        }
        
        // Revert castling and en passant state flags
        whiteKingMoved = oldWhiteKingMoved;
        blackKingMoved = oldBlackKingMoved;
        whiteRookKingsideMoved = oldWhiteRookKingsideMoved;
        whiteRookQueensideMoved = oldWhiteRookQueensideMoved;
        blackRookKingsideMoved = oldBlackRookKingsideMoved;
        blackRookQueensideMoved = oldBlackRookQueensideMoved;
        enPassantTarget = oldEnPassantTarget;
        
        return isInCheck;
    }
}