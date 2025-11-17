// /frontend/src/components/Dashboard.jsx
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="auth-wrapper">
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
      </div>
      
      <div className="auth-card">
        <div className="card-glow"></div>
        
        <div className="crown-icon">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M12 2L15 8L21 9L16.5 14L18 21L12 17L6 21L7.5 14L3 9L9 8L12 2Z" />
          </svg>
        </div>
        
        <h1>Welcome, {user?.username}!</h1>
        <p className="subtitle">Your chess realm awaits</p>

        <div className="dashboard-stats">
          <div className="stat-item">
            <div className="stat-number">12</div>
            <div className="stat-label">Games Played</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">8</div>
            <div className="stat-label">Victories</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">67%</div>
            <div className="stat-label">Win Rate</div>
          </div>
        </div>

        <div className="dashboard-actions">
          <button className="primary-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" />
              <polyline points="13 2 13 9 20 9" />
            </svg>
            New Game
          </button>
          
          <button className="secondary-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
              <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
            </svg>
            Analyze Games
          </button>
        </div>

        <div className="divider">
          <span>or</span>
        </div>

        <button 
          className="secondary-btn logout-btn" 
          onClick={handleLogout}
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16,17 21,12 16,7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          Exit Realm
        </button>
      </div>
    </div>
  );
}