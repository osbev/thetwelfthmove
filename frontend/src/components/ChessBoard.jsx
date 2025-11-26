// /frontend/src/components/ChessBoard.jsx
import { useState, useEffect } from "react";
import { getPieceComponent } from "./ChessPieces";
import "../styles/chessboard.css";

export default function ChessBoard({ 
  board, 
  currentTurn, 
  onMove, 
  lastMove,
  isCheck,
  flipped = false 
}) {
  const [selectedSquare, setSelectedSquare] = useState(null);
  const [validMoves, setValidMoves] = useState([]);
  const [draggedPiece, setDraggedPiece] = useState(null);
  const [dragPosition, setDragPosition] = useState(null);

  // Convert row/col to notation (e.g., [0,0] -> "a8")
  const toNotation = (row, col) => {
    const file = String.fromCharCode(97 + col); // 'a' + col
    const rank = 8 - row;
    return `${file}${rank}`;
  };

  // Convert notation to row/col
  const fromNotation = (notation) => {
    const col = notation.charCodeAt(0) - 97;
    const row = 8 - parseInt(notation[1]);
    return [row, col];
  };

  // Get valid moves for a piece based on chess rules
  const getValidMovesForPiece = (row, col) => {
    const piece = board[row][col];
    if (!piece || piece.color !== currentTurn) return [];

    const moves = [];
    
    switch(piece.type) {
      case 'pawn':
        moves.push(...getValidPawnMoves(row, col, piece.color));
        break;
      case 'knight':
        moves.push(...getValidKnightMoves(row, col, piece.color));
        break;
      case 'bishop':
        moves.push(...getValidBishopMoves(row, col, piece.color));
        break;
      case 'rook':
        moves.push(...getValidRookMoves(row, col, piece.color));
        break;
      case 'queen':
        moves.push(...getValidQueenMoves(row, col, piece.color));
        break;
      case 'king':
        moves.push(...getValidKingMoves(row, col, piece.color));
        break;
    }

    return moves;
  };

  // Pawn moves (includes en passant possibility)
  const getValidPawnMoves = (row, col, color) => {
    const moves = [];
    const direction = color === 'white' ? -1 : 1;
    const startRow = color === 'white' ? 6 : 1;

    // One square forward
    const newRow = row + direction;
    if (newRow >= 0 && newRow < 8 && !board[newRow][col]) {
      moves.push(toNotation(newRow, col));

      // Two squares forward from start
      if (row === startRow) {
        const twoRow = row + (2 * direction);
        if (!board[twoRow][col]) {
          moves.push(toNotation(twoRow, col));
        }
      }
    }

    // Diagonal captures (regular and en passant)
    [-1, 1].forEach(colOffset => {
      const newCol = col + colOffset;
      if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
        const target = board[newRow][newCol];
        // Regular capture
        if (target && target.color !== color) {
          moves.push(toNotation(newRow, newCol));
        }
        // En passant - show diagonal move even if square is empty
        // Backend will validate if en passant is legal
        else if (!target) {
          const adjacentPiece = board[row][newCol];
          if (adjacentPiece && adjacentPiece.type === 'pawn' && adjacentPiece.color !== color) {
            // Might be en passant, show as valid move
            moves.push(toNotation(newRow, newCol));
          }
        }
      }
    });

    return moves;
  };

  // Knight moves
  const getValidKnightMoves = (row, col, color) => {
    const moves = [];
    const knightMoves = [
      [-2, -1], [-2, 1], [-1, -2], [-1, 2],
      [1, -2], [1, 2], [2, -1], [2, 1]
    ];

    knightMoves.forEach(([dRow, dCol]) => {
      const newRow = row + dRow;
      const newCol = col + dCol;
      if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
        const target = board[newRow][newCol];
        if (!target || target.color !== color) {
          moves.push(toNotation(newRow, newCol));
        }
      }
    });

    return moves;
  };

  // Bishop moves (diagonals)
  const getValidBishopMoves = (row, col, color) => {
    const moves = [];
    const directions = [[-1, -1], [-1, 1], [1, -1], [1, 1]];

    directions.forEach(([dRow, dCol]) => {
      let newRow = row + dRow;
      let newCol = col + dCol;

      while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
        const target = board[newRow][newCol];
        if (!target) {
          moves.push(toNotation(newRow, newCol));
        } else {
          if (target.color !== color) {
            moves.push(toNotation(newRow, newCol));
          }
          break;
        }
        newRow += dRow;
        newCol += dCol;
      }
    });

    return moves;
  };

  // Rook moves (straight lines)
  const getValidRookMoves = (row, col, color) => {
    const moves = [];
    const directions = [[-1, 0], [1, 0], [0, -1], [0, 1]];

    directions.forEach(([dRow, dCol]) => {
      let newRow = row + dRow;
      let newCol = col + dCol;

      while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
        const target = board[newRow][newCol];
        if (!target) {
          moves.push(toNotation(newRow, newCol));
        } else {
          if (target.color !== color) {
            moves.push(toNotation(newRow, newCol));
          }
          break;
        }
        newRow += dRow;
        newCol += dCol;
      }
    });

    return moves;
  };

  // Queen moves (bishop + rook)
  const getValidQueenMoves = (row, col, color) => {
    return [
      ...getValidBishopMoves(row, col, color),
      ...getValidRookMoves(row, col, color)
    ];
  };

  // King moves (includes castling)
  const getValidKingMoves = (row, col, color) => {
    const moves = [];
    const directions = [
      [-1, -1], [-1, 0], [-1, 1],
      [0, -1], [0, 1],
      [1, -1], [1, 0], [1, 1]
    ];

    // Normal king moves (one square in any direction)
    directions.forEach(([dRow, dCol]) => {
      const newRow = row + dRow;
      const newCol = col + dCol;
      if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
        const target = board[newRow][newCol];
        if (!target || target.color !== color) {
          moves.push(toNotation(newRow, newCol));
        }
      }
    });

    // Castling (kingside and queenside)
    // Note: Backend will validate if castling is actually legal
    // We just show it as a possible move if king hasn't moved
    const isStartingPosition = (color === 'white' && row === 7 && col === 4) ||
                               (color === 'black' && row === 0 && col === 4);
    
    if (isStartingPosition) {
      // Kingside castling (king moves 2 squares right)
      const kingsideTarget = board[row][col + 1];
      const kingsideTarget2 = board[row][col + 2];
      if (!kingsideTarget && !kingsideTarget2) {
        moves.push(toNotation(row, col + 2));
      }
      
      // Queenside castling (king moves 2 squares left)
      const queensideTarget = board[row][col - 1];
      const queensideTarget2 = board[row][col - 2];
      const queensideTarget3 = board[row][col - 3];
      if (!queensideTarget && !queensideTarget2 && !queensideTarget3) {
        moves.push(toNotation(row, col - 2));
      }
    }

    return moves;
  };

  // Handle square click
  const handleSquareClick = (row, col) => {
    const notation = toNotation(row, col);
    const piece = board[row][col];

    // If no piece selected, select this piece if it's the current player's
    if (!selectedSquare) {
      if (piece && piece.color === currentTurn) {
        setSelectedSquare(notation);
        setValidMoves(getValidMovesForPiece(row, col));
      }
      return;
    }

    // If clicking the same square, deselect
    if (selectedSquare === notation) {
      setSelectedSquare(null);
      setValidMoves([]);
      return;
    }

    // If clicking own piece, select that instead
    if (piece && piece.color === currentTurn) {
      setSelectedSquare(notation);
      setValidMoves(getValidMovesForPiece(row, col));
      return;
    }

    // Try to make a move
    onMove(selectedSquare, notation);
    setSelectedSquare(null);
    setValidMoves([]);
  };

  // Drag and drop handlers
  const handleDragStart = (e, row, col) => {
    const piece = board[row][col];
    if (!piece || piece.color !== currentTurn) {
      e.preventDefault();
      return;
    }

    const notation = toNotation(row, col);
    setDraggedPiece(notation);
    setValidMoves(getValidMovesForPiece(row, col));

    // Create custom drag image
    const dragImg = e.target.cloneNode(true);
    dragImg.style.opacity = '0.5';
    document.body.appendChild(dragImg);
    e.dataTransfer.setDragImage(dragImg, 32, 32);
    setTimeout(() => document.body.removeChild(dragImg), 0);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  const handleDrop = (e, row, col) => {
    e.preventDefault();
    if (!draggedPiece) return;

    const targetNotation = toNotation(row, col);
    onMove(draggedPiece, targetNotation);
    
    setDraggedPiece(null);
    setValidMoves([]);
  };

  const handleDragEnd = () => {
    setDraggedPiece(null);
    setValidMoves([]);
  };

  // Check if square should be highlighted
  const isHighlighted = (notation) => {
    if (selectedSquare === notation || draggedPiece === notation) return 'selected';
    if (validMoves.includes(notation)) return 'valid-move';
    if (lastMove && (lastMove.from === notation || lastMove.to === notation)) return 'last-move';
    return '';
  };

  // Check if king is in check on this square
  const isKingInCheck = (row, col) => {
    const piece = board[row][col];
    return isCheck && piece && piece.type === 'king' && piece.color === currentTurn;
  };

  // Render a single square
  const renderSquare = (row, col) => {
    const displayRow = flipped ? 7 - row : row;
    const displayCol = flipped ? 7 - col : col;
    const piece = board[displayRow][displayCol];
    const notation = toNotation(displayRow, displayCol);
    const isLight = (row + col) % 2 === 0;
    const highlight = isHighlighted(notation);
    const kingCheck = isKingInCheck(displayRow, displayCol);

    const PieceComponent = piece ? getPieceComponent(piece.type, piece.color) : null;

    return (
      <div
        key={`${row}-${col}`}
        className={`square ${isLight ? 'light' : 'dark'} ${highlight} ${kingCheck ? 'in-check' : ''}`}
        onClick={() => handleSquareClick(displayRow, displayCol)}
        onDragOver={handleDragOver}
        onDrop={(e) => handleDrop(e, displayRow, displayCol)}
      >
        {/* Coordinate labels */}
        {col === 0 && (
          <span className="rank-label">{flipped ? row + 1 : 8 - row}</span>
        )}
        {row === 7 && (
          <span className="file-label">{String.fromCharCode(97 + (flipped ? 7 - col : col))}</span>
        )}

        {/* Piece */}
        {piece && (
          <div
            className={`piece ${piece.color} ${draggedPiece === notation ? 'dragging' : ''}`}
            draggable={piece.color === currentTurn}
            onDragStart={(e) => handleDragStart(e, displayRow, displayCol)}
            onDragEnd={handleDragEnd}
          >
            {PieceComponent && <PieceComponent />}
          </div>
        )}

        {/* Valid move indicator */}
        {validMoves.includes(notation) && (
          <div className={`move-indicator ${piece ? 'capture' : 'empty'}`} />
        )}
      </div>
    );
  };

  return (
    <div className="chessboard-wrapper">
      <div className="chessboard">
        {Array.from({ length: 8 }).map((_, row) => (
          <div key={row} className="board-row">
            {Array.from({ length: 8 }).map((_, col) => renderSquare(row, col))}
          </div>
        ))}
      </div>
    </div>
  );
}