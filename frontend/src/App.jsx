// /frontend/src/App.jsx
import { useState } from "react";
import Login from "./components/Login";
import Signup from "./components/Signup";
import "./styles/root.css";
import "./styles/main.css";

export default function App() {
  const [showLogin, setShowLogin] = useState(true);

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
          <Login switchToSignup={() => setShowLogin(false)} />
        ) : (
          <Signup switchToLogin={() => setShowLogin(true)} />
        )}
      </div>
    </div>
  );
}