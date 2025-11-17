// /frontend/src/components/AuthPage.jsx
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Login from "./Login";
import Signup from "./Signup";

export default function AuthPage({ isSignup = false }) {
  const [showLogin, setShowLogin] = useState(!isSignup);
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  // Redirect to dashboard if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate("/dashboard", { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const switchToSignup = () => {
    setShowLogin(false);
    navigate("/signup");
  };

  const switchToLogin = () => {
    setShowLogin(true);
    navigate("/login");
  };

  return (
    <div className="auth-wrapper">
      <div className="chess-pieces-bg">
        <div className="piece piece-1">♔</div>
        <div className="piece piece-2">♛</div>
        <div className="piece piece-3">♜</div>
        <div className="piece piece-4">♝</div>
      </div>
      
      <div className={`page-transition ${showLogin ? 'login-active' : 'signup-active'}`}>
        {showLogin ? (
          <Login switchToSignup={switchToSignup} />
        ) : (
          <Signup switchToLogin={switchToLogin} />
        )}
      </div>
    </div>
  );
}