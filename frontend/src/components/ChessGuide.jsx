// /frontend/src/components/ChessGuide.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/guide.css";

export default function ChessGuide() {
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState("basics");

  const sections = {
    basics: {
      title: "Chess Basics",
      icon: "♟️",
      content: [
        {
          subtitle: "The Board",
          text: "Chess is played on an 8x8 board with 64 squares alternating between light and dark colors. Each player starts with 16 pieces."
        },
        {
          subtitle: "The Objective",
          text: "The goal is to checkmate your opponent's king. This means the king is under attack and has no legal move to escape."
        },
        {
          subtitle: "How Pieces Move",
          text: "Each piece type has unique movement rules. Understanding these is fundamental to playing chess."
        }
      ]
    },
    pieces: {
      title: "Piece Movement",
      icon: "♔",
      content: [
        {
          subtitle: "Pawn ♙",
          text: "Moves forward one square (or two on first move). Captures diagonally. Can promote to any piece upon reaching the opposite end."
        },
        {
          subtitle: "Knight ♘",
          text: "Moves in an 'L' shape: two squares in one direction, one square perpendicular. Can jump over other pieces."
        },
        {
          subtitle: "Bishop ♗",
          text: "Moves diagonally any number of squares. Each bishop stays on its starting color throughout the game."
        },
        {
          subtitle: "Rook ♖",
          text: "Moves horizontally or vertically any number of squares. Essential for controlling files and ranks."
        },
        {
          subtitle: "Queen ♕",
          text: "The most powerful piece. Moves like both a rook and bishop combined - any number of squares in any direction."
        },
        {
          subtitle: "King ♔",
          text: "Moves one square in any direction. Must be protected at all costs. Can castle once per game under specific conditions."
        }
      ]
    },
    special: {
      title: "Special Moves",
      icon: "✨",
      content: [
        {
          subtitle: "Castling",
          text: "A special move involving the king and rook. The king moves two squares toward a rook, and the rook jumps over to the square next to the king. Can only be done if neither piece has moved, squares between are empty, and the king isn't in check or moving through check."
        },
        {
          subtitle: "En Passant",
          text: "A special pawn capture. If an opponent's pawn moves two squares forward from its starting position and lands beside your pawn, you can capture it 'in passing' by moving diagonally to the square it skipped over. Must be done immediately on the next turn."
        },
        {
          subtitle: "Pawn Promotion",
          text: "When a pawn reaches the opposite end of the board, it must be promoted to a queen, rook, bishop, or knight (player's choice). Most players choose a queen."
        }
      ]
    },
    strategy: {
      title: "Basic Strategy",
      icon: "🧠",
      content: [
        {
          subtitle: "Control the Center",
          text: "The four central squares (e4, e5, d4, d5) are the most important on the board. Pieces in the center have more mobility and influence."
        },
        {
          subtitle: "Develop Your Pieces",
          text: "Bring your knights and bishops into play early. Don't move the same piece multiple times in the opening unless necessary."
        },
        {
          subtitle: "King Safety",
          text: "Castle early to protect your king. Keep pawns in front of your castled king when possible."
        },
        {
          subtitle: "Think Ahead",
          text: "Always look for your opponent's threats before making a move. Try to think 2-3 moves ahead."
        },
        {
          subtitle: "Piece Value",
          text: "Pawn = 1, Knight = 3, Bishop = 3, Rook = 5, Queen = 9. Use this as a guide for trading pieces, but position and tactics matter more than raw material."
        }
      ]
    },
    tactics: {
      title: "Common Tactics",
      icon: "⚔️",
      content: [
        {
          subtitle: "Fork",
          text: "Attacking two or more pieces simultaneously with one piece. Knights are especially good at forks."
        },
        {
          subtitle: "Pin",
          text: "Attacking a piece that can't move without exposing a more valuable piece behind it."
        },
        {
          subtitle: "Skewer",
          text: "Like a reverse pin - attacking a valuable piece, forcing it to move and exposing a less valuable piece behind it."
        },
        {
          subtitle: "Discovered Attack",
          text: "Moving a piece reveals an attack from another piece behind it."
        },
        {
          subtitle: "Double Attack",
          text: "Threatening two things at once, forcing your opponent to save one while losing the other."
        }
      ]
    }
  };

  return (
    <div className="guide-container">
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
      </div>

      <div className="guide-content">
        <div className="guide-header">
          <button className="back-btn" onClick={() => navigate('/dashboard')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
            Back
          </button>
          <div className="header-title">
            <h1>📚 Learn Chess</h1>
            <p>Master the game of kings</p>
          </div>
        </div>

        <div className="guide-navigation">
          {Object.entries(sections).map(([key, section]) => (
            <button
              key={key}
              className={`nav-btn ${activeSection === key ? 'active' : ''}`}
              onClick={() => setActiveSection(key)}
            >
              <span className="nav-icon">{section.icon}</span>
              <span className="nav-text">{section.title}</span>
            </button>
          ))}
        </div>

        <div className="guide-section">
          <h2>{sections[activeSection].title}</h2>
          <div className="section-content">
            {sections[activeSection].content.map((item, index) => (
              <div key={index} className="content-card">
                <h3>{item.subtitle}</h3>
                <p>{item.text}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="guide-tips">
          <div className="tip-card">
            <span className="tip-icon">💡</span>
            <div className="tip-content">
              <h4>Pro Tip</h4>
              <p>Practice makes perfect! Play regularly and analyze your games to improve. Don't be afraid to experiment with different openings and strategies.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}