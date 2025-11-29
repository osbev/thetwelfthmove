// /frontend/src/components/Leaderboard.jsx
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/leaderboard.css";

export default function Leaderboard() {
  const navigate = useNavigate();
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sortBy, setSortBy] = useState("winRate");

  useEffect(() => {
    loadLeaderboard();
  }, []);

  const loadLeaderboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get('http://localhost:7000/players/leaderboard');
      setPlayers(response.data.players || []);
    } catch (err) {
      console.error('Failed to load leaderboard:', err);
      setError('Failed to load leaderboard. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const sortedPlayers = [...players].sort((a, b) => {
    switch(sortBy) {
      case "winRate":
        const aRate = a.gamesPlayed > 0 ? (a.gamesWon / a.gamesPlayed) : 0;
        const bRate = b.gamesPlayed > 0 ? (b.gamesWon / b.gamesPlayed) : 0;
        return bRate - aRate;
      case "gamesWon":
        return b.gamesWon - a.gamesWon;
      case "gamesPlayed":
        return b.gamesPlayed - a.gamesPlayed;
      default:
        return 0;
    }
  });

  const getRankIcon = (index) => {
    switch(index) {
      case 0: return "🥇";
      case 1: return "🥈";
      case 2: return "🥉";
      default: return `#${index + 1}`;
    }
  };

  const getWinRate = (player) => {
    if (player.gamesPlayed === 0) return "0.0";
    return ((player.gamesWon / player.gamesPlayed) * 100).toFixed(1);
  };

  if (loading) {
    return (
      <div className="leaderboard-container">
        <div className="chess-pieces-bg">
          <div className="piece piece-1">♔</div>
          <div className="piece piece-2">♛</div>
          <div className="piece piece-3">♜</div>
          <div className="piece piece-4">♝</div>
        </div>
        <div className="leaderboard-content">
          <div className="leaderboard-header">
            <button className="back-btn" onClick={() => navigate('/dashboard')}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              Back
            </button>
            <h1>Leaderboard</h1>
          </div>
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading rankings...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="leaderboard-container">
        <div className="chess-pieces-bg">
          <div className="piece piece-1">♔</div>
          <div className="piece piece-2">♛</div>
          <div className="piece piece-3">♜</div>
          <div className="piece piece-4">♝</div>
        </div>
        <div className="leaderboard-content">
          <div className="leaderboard-header">
            <button className="back-btn" onClick={() => navigate('/dashboard')}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              Back
            </button>
            <h1>Leaderboard</h1>
          </div>
          <div className="error-state">
            <p>{error}</p>
            <button className="retry-btn" onClick={loadLeaderboard}>Try Again</button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="leaderboard-container">
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
      </div>

      <div className="leaderboard-content">
        <div className="leaderboard-header">
          <button className="back-btn" onClick={() => navigate('/dashboard')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
            Back
          </button>
          <div className="header-title">
            <h1>🏆 Leaderboard</h1>
            <p>Top chess players</p>
          </div>
        </div>

        <div className="sort-controls">
          <button 
            className={`sort-btn ${sortBy === 'winRate' ? 'active' : ''}`}
            onClick={() => setSortBy('winRate')}
          >
            Win Rate
          </button>
          <button 
            className={`sort-btn ${sortBy === 'gamesWon' ? 'active' : ''}`}
            onClick={() => setSortBy('gamesWon')}
          >
            Games Won
          </button>
          <button 
            className={`sort-btn ${sortBy === 'gamesPlayed' ? 'active' : ''}`}
            onClick={() => setSortBy('gamesPlayed')}
          >
            Games Played
          </button>
        </div>

        {sortedPlayers.length > 0 ? (
          <div className="leaderboard-list">
            {sortedPlayers.map((player, index) => (
              <div key={player.id} className={`player-card ${index < 3 ? 'top-player' : ''}`}>
                <div className="player-rank">
                  {getRankIcon(index)}
                </div>
                <div className="player-info">
                  <div className="player-avatar">
                    {player.username.charAt(0).toUpperCase()}
                  </div>
                  <div className="player-details">
                    <h3>{player.username}</h3>
                    <p className="player-stats">
                      {player.gamesWon}W - {player.gamesPlayed - player.gamesWon}L
                    </p>
                  </div>
                </div>
                <div className="player-metrics">
                  <div className="metric">
                    <span className="metric-label">Win Rate</span>
                    <span className="metric-value">{getWinRate(player)}%</span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">Games</span>
                    <span className="metric-value">{player.gamesPlayed}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" />
            </svg>
            <p>No players yet</p>
            <p className="empty-subtitle">Be the first to play and claim the top spot!</p>
          </div>
        )}
      </div>
    </div>
  );
}