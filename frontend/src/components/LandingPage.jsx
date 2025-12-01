// /frontend/src/components/LandingPage.jsx
import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ChessBoard from "./ChessBoard";
import "../styles/landing.css";

// Predefined demo moves for interactive experience
const DEMO_MOVES = [
  { from: "e2", to: "e4" },
  { from: "e7", to: "e5" },
  { from: "g1", to: "f3" },
  { from: "b8", to: "c6" },
  { from: "f1", to: "c4" },
  { from: "g8", to: "f6" },
  { from: "d2", to: "d4" },
  { from: "e5", to: "d4" },
];

export default function LandingPage() {
  const navigate = useNavigate();
  const [moveCount, setMoveCount] = useState(0);
  const [autoPlaying, setAutoPlaying] = useState(false);
  const autoPlayInterval = useRef(null);
  const [showOverlay, setShowOverlay] = useState(false);
  
  // Initialize board with more elegant structure
  const initialBoard = () => [
    [
      { type: 'rook', color: 'black' },
      { type: 'knight', color: 'black' },
      { type: 'bishop', color: 'black' },
      { type: 'queen', color: 'black' },
      { type: 'king', color: 'black' },
      { type: 'bishop', color: 'black' },
      { type: 'knight', color: 'black' },
      { type: 'rook', color: 'black' }
    ],
    Array(8).fill(null).map(() => ({ type: 'pawn', color: 'black' })),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null).map(() => ({ type: 'pawn', color: 'white' })),
    [
      { type: 'rook', color: 'white' },
      { type: 'knight', color: 'white' },
      { type: 'bishop', color: 'white' },
      { type: 'queen', color: 'white' },
      { type: 'king', color: 'white' },
      { type: 'bishop', color: 'white' },
      { type: 'knight', color: 'white' },
      { type: 'rook', color: 'white' }
    ]
  ];
  
  const [board, setBoard] = useState(initialBoard());
  const [currentTurn, setCurrentTurn] = useState('white');
  const [lastMove, setLastMove] = useState(null);
  const [isAnimating, setIsAnimating] = useState(false);

  const fromNotation = (notation) => {
    const col = notation.charCodeAt(0) - 97;
    const row = 8 - parseInt(notation[1]);
    return [row, col];
  };

  const handleDemoMove = (from, to, instant = false) => {
    if (isAnimating && !instant) return;
    
    setIsAnimating(true);
    const [fromRow, fromCol] = fromNotation(from);
    const [toRow, toCol] = fromNotation(to);
    
    setTimeout(() => {
      const newBoard = board.map(row => [...row]);
      newBoard[toRow][toCol] = newBoard[fromRow][fromCol];
      newBoard[fromRow][fromCol] = null;
      
      setBoard(newBoard);
      setLastMove({ from, to });
      setCurrentTurn(currentTurn === 'white' ? 'black' : 'white');
      setMoveCount(prev => prev + 1);
      setIsAnimating(false);
      
      if (moveCount + 1 >= 5) {
        setShowOverlay(true);
      }
    }, instant ? 0 : 300);
  };

  const handleReset = () => {
    setBoard(initialBoard());
    setCurrentTurn('white');
    setLastMove(null);
    setMoveCount(0);
    setShowOverlay(false);
    if (autoPlaying) {
      stopAutoPlay();
    }
  };

  const startAutoPlay = () => {
    if (autoPlaying) return;
    setAutoPlaying(true);
    handleReset();
    
    let moveIndex = 0;
    autoPlayInterval.current = setInterval(() => {
      if (moveIndex >= DEMO_MOVES.length * 2) {
        stopAutoPlay();
        return;
      }
      
      const move = DEMO_MOVES[Math.floor(moveIndex / 2)];
      if (moveIndex % 2 === 0) {
        handleDemoMove(move.from, move.to, true);
      }
      moveIndex++;
    }, 1000);
  };

  const stopAutoPlay = () => {
    if (autoPlayInterval.current) {
      clearInterval(autoPlayInterval.current);
      autoPlayInterval.current = null;
    }
    setAutoPlaying(false);
  };

  useEffect(() => {
    return () => {
      if (autoPlayInterval.current) {
        clearInterval(autoPlayInterval.current);
      }
    };
  }, []);

  const handleAutoPlayToggle = () => {
    if (autoPlaying) {
      stopAutoPlay();
    } else {
      startAutoPlay();
    }
  };

  const FeatureCard = ({ icon, title, description, action }) => (
    <div className="feature-card" onClick={action}>
      <div className="feature-icon">{icon}</div>
      <h3>{title}</h3>
      <p>{description}</p>
      <div className="feature-action">
        <span>Explore →</span>
      </div>
    </div>
  );

  return (
    <div className="landing-page">
      {/* Animated chess pieces background */}
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
        <div className="piece piece-5">♞</div>
        <div className="piece piece-6">♟</div>
      </div>

      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <div className="hero-text">
            <div className="eyebrow">CHESS REDEFINED</div>
            <h1 className="brand-title">
              Master the<br />
              <span className="highlight">Twelfth Move</span>
            </h1>
            
            <p className="tagline">
              Elevate your game beyond the obvious. In chess, as in life, 
              victory lies in seeing what others don't.
            </p>

            <div className="stats-grid">
              <div className="stat">
                <div className="stat-value">11</div>
                <div className="stat-label">Obvious Moves</div>
              </div>
              <div className="stat-divider"></div>
              <div className="stat">
                <div className="stat-value">1</div>
                <div className="stat-label">Master Move</div>
              </div>
              <div className="stat-divider"></div>
              <div className="stat">
                <div className="stat-value">∞</div>
                <div className="stat-label">Possibilities</div>
              </div>
            </div>

            <div className="hero-actions">
              <button className="btn-primary" onClick={() => navigate("/register")}>
                Begin Your Journey
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </button>
              <button className="btn-secondary" onClick={handleAutoPlayToggle}>
                {autoPlaying ? 'Stop Demo' : 'Watch Demo'}
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  {autoPlaying ? (
                    <rect x="6" y="6" width="12" height="12" rx="1"/>
                  ) : (
                    <path d="M5 3l14 9-14 9V3z"/>
                  )}
                </svg>
              </button>
            </div>
          </div>

          {/* Interactive Demo Board */}
          <div className="demo-section">
            <div className="demo-container">
              <div className="demo-header">
                <div className="demo-title">
                  <div className="demo-icon">♟</div>
                  <div>
                    <h3>Interactive Demo</h3>
                    <p>Experience real chess gameplay</p>
                  </div>
                </div>
                
                <div className="demo-controls">
                  <div className="turn-indicator">
                    <div className={`turn-dot ${currentTurn}`}></div>
                    <span>{currentTurn === "white" ? "White" : "Black"} to move</span>
                  </div>
                  
                  <div className="control-buttons">
                    <button 
                      className={`control-btn ${autoPlaying ? 'active' : ''}`}
                      onClick={handleAutoPlayToggle}
                      title={autoPlaying ? "Stop auto-play" : "Start auto-play"}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        {autoPlaying ? (
                          <rect x="6" y="6" width="12" height="12" rx="1"/>
                        ) : (
                          <path d="M5 3l14 9-14 9V3z"/>
                        )}
                      </svg>
                    </button>
                    
                    <button className="control-btn" onClick={handleReset} title="Reset board">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
                        <path d="M3 3v5h5"/>
                        <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L21 16"/>
                        <path d="M21 21v-5h-5"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
              
              <div className="board-wrapper">
                <ChessBoard
                  board={board}
                  currentTurn={currentTurn}
                  onMove={handleDemoMove}
                  lastMove={lastMove}
                  isCheck={false}
                  flipped={false}
                  interactive={true}
                />
              </div>
              
              <div className="demo-footer">
                <div className="move-counter">
                  <span>Moves: {moveCount}</span>
                </div>
                <div className="demo-hint">
                  <span className="hint-icon">💡</span>
                  Click on pieces to make moves
                </div>
              </div>
            </div>

            {/* Feature Cards */}
            <div className="features-grid">
              <FeatureCard
                icon="🎮"
                title="Local Play"
                description="Pass and play on the same device with friends"
                action={() => navigate("/register")}
              />
              <FeatureCard
                icon="🌐"
                title="Online Multiplayer"
                description="Challenge friends online with game codes"
                action={() => navigate("/register")}
              />
              <FeatureCard
                icon="🏆"
                title="Leaderboards"
                description="Compete and climb the rankings"
                action={() => navigate("/register")}
              />
              <FeatureCard
                icon="📜"
                title="Game History"
                description="Review and analyze all your past matches"
                action={() => navigate("/register")}
              />
              <FeatureCard
                icon="📚"
                title="Learn Chess"
                description="Master strategies with our study guide"
                action={() => navigate("/register")}
              />
              <FeatureCard
                icon="🔗"
                title="Join Games"
                description="Enter codes to join friend's matches"
                action={() => navigate("/register")}
              />
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta-section">
        <div className="cta-content">
          <h2>Ready for Your Twelfth Move?</h2>
          <p>
            Join thousands of players who've elevated their game. 
            Your journey to mastery starts with a single move.
          </p>
          <div className="cta-stats">
            <div className="cta-stat">
              <div className="cta-stat-icon">👥</div>
              <div className="cta-stat-value">1000+</div>
              <div className="cta-stat-label">Active Players</div>
            </div>
            <div className="cta-stat-divider"></div>
            <div className="cta-stat">
              <div className="cta-stat-icon">♟️</div>
              <div className="cta-stat-value">5000+</div>
              <div className="cta-stat-label">Games Played</div>
            </div>
            <div className="cta-stat-divider"></div>
            <div className="cta-stat">
              <div className="cta-stat-icon">⚡</div>
              <div className="cta-stat-value">24/7</div>
              <div className="cta-stat-label">Available</div>
            </div>
          </div>
          <button className="btn-cta" onClick={() => navigate("/register")}>
            Claim Your Place at the Board
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
        </div>
      </section>

      {/* Footer */}
      <footer className="landing-footer">
        <div className="footer-content">
          <div className="footer-brand">
            <div className="footer-logo"><img src="../../public/favicon.png" alt="The Twelfth Move" /></div>
            <div className="footer-title">The Twelfth Move</div>
          </div>
          <p className="footer-quote">
            "In every position, there are eleven moves everyone sees. 
            The twelfth is the one that makes a master."
          </p>
          <div className="footer-links">
            <a href="#">About</a>
            <a href="#">Community</a>
            <a href="#">Help</a>
            <a href="#">Contact</a>
          </div>
          <p className="footer-copyright">
            © 2025 The Twelfth Move. All rights reserved.
          </p>
        </div>
      </footer>

      {/* Overlay */}
      {showOverlay && (
        <div className="mastery-overlay">
          <div className="overlay-content">
            <button className="overlay-close" onClick={() => setShowOverlay(false)}>
              ×
            </button>
            <div className="overlay-icon">♔</div>
            <h3>You've Seen the Moves</h3>
            <p>
              Now experience the full game. 
              Join our community to play online, track your progress, 
              and compete on the leaderboards.
            </p>
            <div className="overlay-actions">
              <button className="btn-primary" onClick={() => navigate("/register")}>
                Continue to Mastery
              </button>
              <button className="btn-secondary" onClick={() => setShowOverlay(false)}>
                Keep Exploring
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}