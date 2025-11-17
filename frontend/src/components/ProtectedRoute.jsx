// /frontend/src/components/ProtectedRoute.jsx
import { useAuth } from '../context/AuthContext';
import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="auth-wrapper">
        <div className="chess-pieces-bg">
          <div className="piece piece-1">♔</div>
          <div className="piece piece-2">♛</div>
          <div className="piece piece-3">♜</div>
          <div className="piece piece-4">♝</div>
        </div>
        <div className="auth-card">
          <div className="crown-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L15 8L21 9L16.5 14L18 21L12 17L6 21L7.5 14L3 9L9 8L12 2Z" />
            </svg>
          </div>
          <h1>Loading...</h1>
          <p className="subtitle">Preparing your chess realm</p>
          <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
            <svg className="spinner" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 12a9 9 0 1 1-6.219-8.56" />
            </svg>
          </div>
        </div>
      </div>
    );
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />;
}