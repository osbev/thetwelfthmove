// /frontend/src/components/GamePage.jsx
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import ChessBoard from "./ChessBoard";
import "../styles/gamepage.css";

export default function GamePage() {
  const { gameId } = useParams();
  const navigate = useNavigate();
  const { token, user } = useAuth();

  const [game, setGame] = useState(null);
  const [board, setBoard] = useState(null);
  const [moves, setMoves] = useState([]);
  const [capturedPieces, setCapturedPieces] = useState({ white: [], black: [] });
  const [isCheck, setIsCheck] = useState(false);
  const [isCheckmate, setIsCheckmate] = useState(false);
  const [lastMove, setLastMove] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [flipped, setFlipped] = useState(false);

  // Load game state
  useEffect(() => {
    loadGame();
    loadMoves();
  }, [gameId]);

  // Auto-flip board based on turn (for 2-player local)
  useEffect(() => {
    if (game && game.currentTurn === 'black') {
      setFlipped(true);
    } else {
      setFlipped(false);
    }
  }, [game?.currentTurn]);

  const loadGame = async () => {
    try {
      const response = await axios.get(`http://localhost:7000/games/${gameId}`);
      setGame(response.data.game);
      setBoard(response.data.board);
      setLoading(false);
    } catch (err) {
      setError("Failed to load game");
      setLoading(false);
    }
  };

  const loadMoves = async () => {
    try {
      const response = await axios.get(`http://localhost:7000/games/${gameId}/moves`);
      setMoves(response.data.moves);

      const captured = { white: [], black: [] };
      response.data.moves.forEach(move => {
        if (move.capturedPiece) {
          if (move.pieceColor === 'white') {
            captured.black.push(move.capturedPiece);
          } else {
            captured.white.push(move.capturedPiece);
          }
        }
      });
      setCapturedPieces(captured);

      if (response.data.moves.length > 0) {
        const last = response.data.moves[response.data.moves.length - 1];
        setLastMove({ from: last.fromSquare, to: last.toSquare });
      }
    } catch (err) {
      console.error("Failed to load moves", err);
    }
  };

  const handleMove = async (from, to) => {
    try {
      const response = await axios.post(
        `http://localhost:7000/games/${gameId}/move`,
        { from, to },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setBoard(response.data.board);
      setIsCheck(response.data.isCheck);
      setIsCheckmate(response.data.isCheckmate);
      setLastMove({ from, to });

      setGame(prev => ({
        ...prev,
        currentTurn: response.data.nextTurn,
      }));

      loadMoves();

      if (response.data.isCheckmate) {
        setTimeout(() => {
          alert(`Checkmate! ${game.currentTurn === 'white' ? 'White' : 'Black'} wins!`);
        }, 500);
      } else if (response.data.isCheck) {
        setTimeout(() => {
          alert(`Check!`);
        }, 300);
      }
    } catch (err) {
      console.error("Move failed:", err);
      alert(err.response?.data?.error || "Invalid move");
    }
  };

  const handleResign = async () => {
    if (!window.confirm("Are you sure you want to resign?")) return;

    try {
      await axios.post(
        `http://localhost:7000/games/${gameId}/resign`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      alert("You resigned. Returning to dashboard...");
      navigate("/dashboard");
    } catch (err) {
      console.error("Resign failed:", err);
      alert("Failed to resign");
    }
  };

  const handleFlipBoard = () => {
    setFlipped(!flipped);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner">
          <svg className="spinner" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M21 12a9 9 0 1 1-6.219-8.56" />
          </svg>
          <p>Loading game...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p>{error}</p>
        <button onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
      </div>
    );
  }

  return (
    <div className="game-page">
      <div className="game-content">

        {/* LEFT PANEL — Status + Actions */}
        <div className="left-panel">
          <div className="status-card">
            <h2>Game Status</h2>
            <div className="turn-indicator">
              <div className={`turn-badge ${game.currentTurn}`}>
                {game.currentTurn === 'white' ? '⚪' : '⚫'} {game.currentTurn.toUpperCase()}'s Turn
              </div>
            </div>
            {isCheck && !isCheckmate && (
              <div className="alert check-alert">⚠️ CHECK!</div>
            )}
            {isCheckmate && (
              <div className="alert checkmate-alert">👑 CHECKMATE!</div>
            )}
          </div>

          <div className="actions-card">
            <button className="action-btn flip-btn" onClick={handleFlipBoard}>
              🔄 Flip Board
            </button>
            <button className="action-btn resign-btn" onClick={handleResign}>
              🏳️ Resign
            </button>
            <button className="action-btn back-btn" onClick={() => navigate("/dashboard")}>
              ← Back to Dashboard
            </button>
          </div>
        </div>

        {/* CENTER — Chessboard */}
        <div className="board-section">
          <ChessBoard
            board={board}
            currentTurn={game.currentTurn}
            onMove={handleMove}
            lastMove={lastMove}
            isCheck={isCheck}
            flipped={flipped}
          />
        </div>

        {/* RIGHT PANEL — Captured + Moves */}
        <div className="right-panel">

          <div className="captured-card">
            <h3>Captured Pieces</h3>
            <div className="captured-section">
              <div className="captured-label">White Captured:</div>
              <div className="captured-pieces">
                {capturedPieces.white.length > 0 ? (
                  capturedPieces.white.map((piece, idx) => (
                    <span key={idx} className="captured-piece">{piece}</span>
                  ))
                ) : (
                  <span className="empty-captured">None</span>
                )}
              </div>
            </div>

            <div className="captured-section">
              <div className="captured-label">Black Captured:</div>
              <div className="captured-pieces">
                {capturedPieces.black.length > 0 ? (
                  capturedPieces.black.map((piece, idx) => (
                    <span key={idx} className="captured-piece">{piece}</span>
                  ))
                ) : (
                  <span className="empty-captured">None</span>
                )}
              </div>
            </div>
          </div>

          <div className="moves-card">
            <h3>Move History</h3>
            <div className="moves-list">
              {moves.length > 0 ? (
                moves.map((move) => (
                  <div key={move.moveId} className="move-item">
                    <span className="move-number">{move.moveNumber}.</span>
                    <span className={`move-color ${move.pieceColor}`}>
                      {move.pieceColor === 'white' ? '⚪' : '⚫'}
                    </span>
                    <span className="move-notation">
                      {move.fromSquare} → {move.toSquare}
                    </span>
                    {move.capturedPiece && <span className="capture-indicator">✕</span>}
                    {move.isCheck && <span className="check-indicator">+</span>}
                    {move.isCheckmate && <span className="checkmate-indicator">#</span>}
                  </div>
                ))
              ) : (
                <p className="empty-moves">No moves yet</p>
              )}
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
