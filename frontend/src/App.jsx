// /frontend/src/App.jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./components/Login";
import Signup from "./components/Signup";
import Dashboard from "./components/Dashboard";
import GamePage from "./components/GamePage";
import Leaderboard from "./components/Leaderboard";
import ChessGuide from "./components/ChessGuide";
import RecentGames from "./components/RecentGames";
import LandingPage from "./components/LandingPage";
import ProtectedRoute from "./components/ProtectedRoute";
import AuthPage from "./components/AuthPage";
import "./styles/root.css";
import "./styles/main.css";

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Landing page - now the default route */}
          <Route path="/" element={<LandingPage />} />
          
          {/* Auth routes */}
          <Route path="/login" element={<AuthPage />} />
          <Route path="/register" element={<AuthPage isSignup />} />
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
          
          <Route 
            path="/leaderboard" 
            element={
              <ProtectedRoute>
                <Leaderboard />
              </ProtectedRoute>
            } 
          />
          
          <Route 
            path="/guide" 
            element={
              <ProtectedRoute>
                <ChessGuide />
              </ProtectedRoute>
            } 
          />
          
          <Route 
            path="/history" 
            element={
              <ProtectedRoute>
                <RecentGames />
              </ProtectedRoute>
            } 
          />
          
          {/* Catch all - redirect to landing page */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}