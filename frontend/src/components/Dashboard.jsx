// /frontend/src/components/Dashboard.jsx
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/dashboard.css";

export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const { token } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  const handleCreateGame = async () => {
    try {
      const response = await axios.post(
        'http://localhost:7000/games/create',
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.data.game) {
        navigate(`/game/${response.data.game.gameId}`);
      }
    } catch (error) {
      console.error('Failed to create game:', error);
      alert('Failed to create game. Please try again.');
    }
  };

  const winRate = user?.gamesPlayed > 0 
    ? ((user.gamesWon / user.gamesPlayed) * 100).toFixed(1) 
    : 0;

  return (
    <div className="dashboard-container">
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
      </div>

      <div className="dashboard-content">
        <div className="dashboard-header">
          <div className="user-info">
            <div className="user-avatar">
              {user?.username?.charAt(0).toUpperCase()}
            </div>
            <div className="user-details">
              <h1>Welcome, {user?.username}</h1>
              <p className="user-subtitle">The board awaits your move</p>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            Logout
          </button>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon">♟️</div>
            <div className="stat-info">
              <p className="stat-label">Games Played</p>
              <p className="stat-value">{user?.gamesPlayed || 0}</p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">👑</div>
            <div className="stat-info">
              <p className="stat-label">Games Won</p>
              <p className="stat-value">{user?.gamesWon || 0}</p>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">📊</div>
            <div className="stat-info">
              <p className="stat-label">Win Rate</p>
              <p className="stat-value">{winRate}%</p>
            </div>
          </div>
        </div>

        <div className="action-grid">
          <div className="action-card">
            <div className="action-icon">⚔️</div>
            <h3>Quick Match</h3>
            <p>Find an opponent and start playing</p>
            <button className="action-btn primary">Find Match</button>
          </div>

          <div className="action-card">
            <div className="action-icon">🎮</div>
            <h3>Create Game</h3>
            <p>Start a new local 2-player chess game</p>
            <button className="action-btn primary" onClick={handleCreateGame}>Create Game</button>
          </div>

          <div className="action-card">
            <div className="action-icon">🏆</div>
            <h3>Leaderboard</h3>
            <p>View top players and rankings</p>
            <button className="action-btn secondary">View Rankings</button>
          </div>
        </div>

        <div className="recent-games">
          <h2>Recent Games</h2>
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2" />
              <path d="M3 9h18M9 21V9" />
            </svg>
            <p>No games played yet</p>
            <p className="empty-subtitle">Start your first match to see your game history</p>
          </div>
        </div>
      </div>
    </div>
  );
}