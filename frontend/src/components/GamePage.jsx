// /frontend/src/components/GamePage.jsx 
import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import ChessBoard from "./ChessBoard";
import WaitingForOpponent from "./WaitingForOpponent";
import GameCodeDisplay from "./GameCodeDisplay";
import useGamePolling from "../hooks/useGamePolling";
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
  const [player1Username, setPlayer1Username] = useState("");
  const [player2Username, setPlayer2Username] = useState("");
  const [isMyTurn, setIsMyTurn] = useState(false);
  
  // Notification states
  const [notification, setNotification] = useState(null);
  const [opponentDisconnected, setOpponentDisconnected] = useState(false);

  // Show notification helper
  const showNotification = (message, type = 'info', duration = 3000) => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), duration);
  };

  // Initial load
  useEffect(() => {
    loadGame();
  }, [gameId]);

  // Polling callback
  const handlePollUpdate = useCallback((data) => {
    if (data.game) {
      setGame(data.game);
      
      if (data.player1Username) setPlayer1Username(data.player1Username);
      if (data.player2Username) setPlayer2Username(data.player2Username);
    }
    
    if (data.board) {
      setBoard(data.board);
    }
    
    if (data.moves) {
      setMoves(data.moves);
      updateCapturedPieces(data.moves);
      
      if (data.moves.length > 0) {
        const last = data.moves[data.moves.length - 1];
        setLastMove({ from: last.fromSquare, to: last.toSquare });
        setIsCheck(last.isCheck);
        setIsCheckmate(last.isCheckmate);
      }
    }
  }, []);

  const isLocalGame = game && game.player1Id === game.player2Id;
  const pollingEnabled = game && !isLocalGame && (game.status === 'ongoing' || game.status === 'waiting');
  const { opponentMoved } = useGamePolling(gameId, handlePollUpdate, pollingEnabled);

  // Auto-flip board based on player
  useEffect(() => {
    if (game && user) {
      const isLocalGame = game.player1Id === game.player2Id;
      
      if (isLocalGame) {
        setFlipped(game.currentTurn === 'black');
        setIsMyTurn(true);
      } else {
        const isPlayer2 = game.player2Id === user.id;
        setFlipped(isPlayer2);
        
        const currentTurn = game.currentTurn;
        const myTurn = (currentTurn === 'white' && game.player1Id === user.id) ||
                       (currentTurn === 'black' && game.player2Id === user.id);
        setIsMyTurn(myTurn);
      }
    }
  }, [game, user]);

  const loadGame = async () => {
    try {
      const response = await axios.get(`http://localhost:7000/games/${gameId}`);
      setGame(response.data.game);
      setBoard(response.data.board);
      setPlayer1Username(response.data.player1Username);
      setPlayer2Username(response.data.player2Username);
      
      const movesResponse = await axios.get(`http://localhost:7000/games/${gameId}/moves`);
      setMoves(movesResponse.data.moves);
      updateCapturedPieces(movesResponse.data.moves);
      
      if (movesResponse.data.moves.length > 0) {
        const last = movesResponse.data.moves[movesResponse.data.moves.length - 1];
        setLastMove({ from: last.fromSquare, to: last.toSquare });
        setIsCheck(last.isCheck);
        setIsCheckmate(last.isCheckmate);
      }
      
      setLoading(false);
    } catch (err) {
      setError("Failed to load game");
      setLoading(false);
    }
  };

  const updateCapturedPieces = (movesList) => {
    const captured = { white: [], black: [] };
    movesList.forEach(move => {
      if (move.capturedPiece) {
        if (move.pieceColor === 'white') {
          captured.black.push(move.capturedPiece);
        } else {
          captured.white.push(move.capturedPiece);
        }
      }
    });
    setCapturedPieces(captured);
  };

  const handleMove = async (from, to, promotionPiece = null) => {
    try {
      const moveData = { from, to };
      if (promotionPiece) {
        moveData.promotion = promotionPiece;
      }

      const response = await axios.post(
        `http://localhost:7000/games/${gameId}/move`,
        moveData,
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

      const movesResponse = await axios.get(`http://localhost:7000/games/${gameId}/moves`);
      setMoves(movesResponse.data.moves);
      updateCapturedPieces(movesResponse.data.moves);

      if (response.data.isCheckmate) {
        setTimeout(() => {
          showNotification(`Checkmate! ${game.currentTurn === 'white' ? 'White' : 'Black'} wins!`, 'success', 5000);
        }, 500);
      } else if (response.data.isCheck) {
        setTimeout(() => {
          showNotification(`Check!`, 'warning', 2000);
        }, 300);
      }
    } catch (err) {
      console.error("Move failed:", err);
      const errorMsg = err.response?.data?.error || "Invalid move";
      
      // Show detailed error explanation
      let explanation = "";
      if (errorMsg.includes("Invalid move")) {
        explanation = "This move doesn't follow the piece's movement rules.";
      } else if (errorMsg.includes("Not your turn")) {
        explanation = "Please wait for your opponent to move.";
      } else if (errorMsg.includes("check")) {
        explanation = "This move would leave your king in check. You must protect your king!";
      } else if (errorMsg.includes("en passant")) {
        explanation = "En passant can only be done immediately after your opponent's pawn moves two squares.";
      } else if (errorMsg.includes("castle")) {
        explanation = "Castling requires: King and rook haven't moved, no pieces between them, and king not in/through check.";
      }
      
      showNotification(
        explanation || errorMsg,
        'error',
        4000
      );
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

      showNotification("You resigned. Returning to dashboard...", 'info', 2000);
      setTimeout(() => navigate("/dashboard"), 2000);
    } catch (err) {
      console.error("Resign failed:", err);
      showNotification("Failed to resign", 'error');
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

  if (game && game.status === 'waiting') {
    return (
      <div className="game-page">
        <WaitingForOpponent gameCode={game.gameCode} />
      </div>
    );
  }

  return (
    <div className="game-page">
      {/* Notifications */}
      {notification && (
        <div className={`game-notification ${notification.type}`}>
          <div className="notification-content">
            <span className="notification-icon">
              {notification.type === 'error' && '⚠️'}
              {notification.type === 'warning' && '⚡'}
              {notification.type === 'success' && '✅'}
              {notification.type === 'info' && 'ℹ️'}
            </span>
            <span className="notification-message">{notification.message}</span>
          </div>
        </div>
      )}

      {/* Opponent moved notification */}
      {opponentMoved && !isLocalGame && (
        <div className="opponent-notification">
          🎯 Opponent moved!
        </div>
      )}

      {/* Opponent disconnected warning */}
      {opponentDisconnected && !isLocalGame && (
        <div className="disconnect-warning">
          ⚠️ Your opponent seems to have disconnected. You can wait or resign.
        </div>
      )}

      <div className="game-content">
        {/* LEFT PANEL */}
        <div className="left-panel">
          <div className="status-card">
            <h2>Game Status</h2>
            
            {game.gameCode && (
              <div className="code-mini">
                <span className="code-label">Game:</span>
                <span className="code-value">{game.gameCode}</span>
              </div>
            )}

            {!isLocalGame && (
              <div className="players-info">
                <div className="player-badge white">
                  ⚪ {player1Username}
                </div>
                <div className="player-badge black">
                  ⚫ {player2Username}
                </div>
              </div>
            )}

            {isLocalGame && (
              <div className="local-game-badge">
                🎮 Local 2-Player
              </div>
            )}

            <div className="turn-indicator">
              <div className={`turn-badge ${game.currentTurn}`}>
                {game.currentTurn === 'white' ? '⚪' : '⚫'} {game.currentTurn.toUpperCase()}'s Turn
              </div>
              {isMyTurn && <p className="your-turn">It's your turn!</p>}
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
              ← Back
            </button>
          </div>
        </div>

        {/* CENTER - BOARD */}
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

        {/* RIGHT PANEL */}
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