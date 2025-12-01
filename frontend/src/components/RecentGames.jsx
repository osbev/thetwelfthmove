// /frontend/src/components/RecentGames.jsx
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import "../styles/recentgames.css";

export default function RecentGames() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState("all");
  const [currentPage, setCurrentPage] = useState(1);
  const gamesPerPage = 5;

  useEffect(() => {
    if (user) {
      loadGames();
    }
  }, [user]);

  useEffect(() => {
    setCurrentPage(1);
  }, [filter]);

  const loadGames = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get(
        `http://localhost:7000/games/player/${user.id}/recent?limit=100`
      );
      setGames(response.data.games || []);
    } catch (err) {
      console.error('Failed to load games:', err);
      setError('Failed to load game history. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const formatGameResult = (gameData) => {
    const game = gameData.game;
    
    if (game.status === 'ongoing') {
      return { icon: '⏳', text: 'In Progress', class: 'ongoing' };
    }
    
    if (gameData.won) {
      return { icon: '🏆', text: 'Victory', class: 'win' };
    } else {
      return { icon: '😔', text: 'Defeat', class: 'loss' };
    }
  };

  const formatGameDate = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
    return date.toLocaleDateString();
  };

  const getGameDuration = (game) => {
    if (!game.endTime) return 'Ongoing';
    
    const start = new Date(game.startTime);
    const end = new Date(game.endTime);
    const durationMs = end - start;
    const minutes = Math.floor(durationMs / 60000);
    
    if (minutes < 1) return '< 1 min';
    if (minutes < 60) return `${minutes} min`;
    const hours = Math.floor(minutes / 60);
    const remainingMins = minutes % 60;
    return `${hours}h ${remainingMins}m`;
  };

  const filteredGames = games.filter(gameData => {
    const game = gameData.game;
    
    switch(filter) {
      case 'wins':
        return gameData.won && game.status !== 'ongoing';
      case 'losses':
        return !gameData.won && game.status !== 'ongoing';
      case 'ongoing':
        return game.status === 'ongoing';
      default:
        return true;
    }
  });

  const stats = {
    total: games.length,
    wins: games.filter(g => g.won && g.game.status !== 'ongoing').length,
    losses: games.filter(g => !g.won && g.game.status !== 'ongoing').length,
    ongoing: games.filter(g => g.game.status === 'ongoing').length
  };

  // Pagination logic
  const totalPages = Math.ceil(filteredGames.length / gamesPerPage);
  const indexOfLastGame = currentPage * gamesPerPage;
  const indexOfFirstGame = indexOfLastGame - gamesPerPage;
  const currentGames = filteredGames.slice(indexOfFirstGame, indexOfLastGame);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  if (loading) {
    return (
      <div className="recent-games-page">
        <div className="chess-pieces-bg">
          <div className="piece piece-1">♔</div>
          <div className="piece piece-2">♛</div>
          <div className="piece piece-3">♜</div>
          <div className="piece piece-4">♝</div>
        </div>
        <div className="recent-games-content">
          <div className="page-header">
            <button className="back-btn" onClick={() => navigate('/dashboard')}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              Back
            </button>
            <div className="header-title">
              <h1>📜 Game History</h1>
              <p>Your complete chess journey</p>
            </div>
          </div>
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading game history...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="recent-games-page">
        <div className="chess-pieces-bg">
          <div className="piece piece-1">♔</div>
          <div className="piece piece-2">♛</div>
          <div className="piece piece-3">♜</div>
          <div className="piece piece-4">♝</div>
        </div>
        <div className="recent-games-content">
          <div className="page-header">
            <button className="back-btn" onClick={() => navigate('/dashboard')}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
              Back
            </button>
            <div className="header-title">
              <h1>📜 Game History</h1>
              <p>Your complete chess journey</p>
            </div>
          </div>
          <div className="error-state">
            <p>{error}</p>
            <button className="retry-btn" onClick={loadGames}>Try Again</button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="recent-games-page">
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
      </div>

      <div className="recent-games-content">
        <div className="page-header">
          <button className="back-btn" onClick={() => navigate('/dashboard')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
            Back
          </button>
          <div className="header-title">
            <h1>📜 Game History</h1>
            <p>Your complete chess journey</p>
          </div>
        </div>

        {/* Stats Overview */}
        <div className="stats-overview">
          <div className="stat-box">
            <span className="stat-number">{stats.total}</span>
            <span className="stat-label">Total Games</span>
          </div>
          <div className="stat-box wins">
            <span className="stat-number">{stats.wins}</span>
            <span className="stat-label">Wins</span>
          </div>
          <div className="stat-box losses">
            <span className="stat-number">{stats.losses}</span>
            <span className="stat-label">Losses</span>
          </div>
          <div className="stat-box ongoing">
            <span className="stat-number">{stats.ongoing}</span>
            <span className="stat-label">Ongoing</span>
          </div>
        </div>

        {/* Filter Buttons */}
        <div className="filter-controls">
          <button 
            className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
            onClick={() => setFilter('all')}
          >
            All Games
          </button>
          <button 
            className={`filter-btn ${filter === 'wins' ? 'active' : ''}`}
            onClick={() => setFilter('wins')}
          >
            Wins
          </button>
          <button 
            className={`filter-btn ${filter === 'losses' ? 'active' : ''}`}
            onClick={() => setFilter('losses')}
          >
            Losses
          </button>
          <button 
            className={`filter-btn ${filter === 'ongoing' ? 'active' : ''}`}
            onClick={() => setFilter('ongoing')}
          >
            Ongoing
          </button>
        </div>

        {/* Games List */}
        {currentGames.length > 0 ? (
          <>
            <div className="games-list">
              {currentGames.map((gameData) => {
                const result = formatGameResult(gameData);
                const game = gameData.game;
                
                return (
                  <div key={game.gameId} className={`game-item ${result.class}`}>
                    <div className={`game-result ${result.class}`}>
                      <span className="result-icon">{result.icon}</span>
                      <span className="result-text">{result.text}</span>
                    </div>
                    
                    <div className="game-details">
                      <p className="game-opponent">vs {gameData.opponentUsername}</p>
                      <div className="game-meta">
                        <span className="game-date">
                          📅 {formatGameDate(game.endTime || game.startTime)}
                        </span>
                        <span className="game-duration">
                          ⏱️ {getGameDuration(game)}
                        </span>
                        {game.gameCode && (
                          <span className="game-code-inline">
                            🔑 {game.gameCode}
                          </span>
                        )}
                      </div>
                    </div>
                    
                    <button 
                      className="view-game-btn"
                      onClick={() => navigate(`/game/${game.gameId}`)}
                    >
                      {game.status === 'ongoing' ? 'Continue' : 'View'}
                    </button>
                  </div>
                );
              })}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="pagination">
                <button 
                  className="pagination-btn"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 1}
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M15 18l-6-6 6-6"/>
                  </svg>
                  Previous
                </button>
                
                <span className="pagination-info">
                  Page {currentPage} of {totalPages}
                </span>
                
                <button 
                  className="pagination-btn"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage === totalPages}
                >
                  Next
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M9 18l6-6-6-6"/>
                  </svg>
                </button>
              </div>
            )}
          </>
        ) : (
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2" />
              <path d="M3 9h18M9 21V9" />
            </svg>
            <p>No games found</p>
            <p className="empty-subtitle">
              {filter === 'all' 
                ? "Start playing to build your game history!" 
                : `You don't have any ${filter} games yet.`}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}