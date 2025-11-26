// /frontend/src/components/WaitingForOpponent.jsx
import GameCodeDisplay from "./GameCodeDisplay";
import "../styles/waiting.css";

export default function WaitingForOpponent({ gameCode }) {
  return (
    <div className="waiting-container">
      <div className="waiting-content">
        <div className="waiting-animation">
          <div className="chess-pieces-floating">
            <span className="float-piece p1">♔</span>
            <span className="float-piece p2">♛</span>
            <span className="float-piece p3">♜</span>
          </div>
        </div>

        <h2 className="waiting-title">Waiting for Opponent...</h2>
        <p className="waiting-subtitle">Share the game code below with your friend to start playing</p>

        <GameCodeDisplay gameCode={gameCode} />

        <div className="waiting-tips">
          <h4>While you wait:</h4>
          <ul>
            <li>💬 Send the game code to your friend</li>
            <li>⏳ They'll join automatically when they enter the code</li>
            <li>♟️ You'll play as White (first move)</li>
          </ul>
        </div>
      </div>
    </div>
  );
}