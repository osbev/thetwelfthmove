// /frontend/src/App.jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./components/Login";
import Signup from "./components/Signup";
import Dashboard from "./components/Dashboard";
import GamePage from "./components/GamePage";
import ProtectedRoute from "./components/ProtectedRoute";
import AuthPage from "./components/AuthPage";
import "./styles/root.css";
import "./styles/main.css";

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Redirect root to login */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          
          {/* Auth routes */}
          <Route path="/login" element={<AuthPage />} />
          <Route path="/signup" element={<AuthPage isSignup />} />
          
          {/* Protected routes */}
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          
          <Route 
            path="/game/:gameId" 
            element={
              <ProtectedRoute>
                <GamePage />
              </ProtectedRoute>
            } 
          />
          
          {/* Catch all - redirect to login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}