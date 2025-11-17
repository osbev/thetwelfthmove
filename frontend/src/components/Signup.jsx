// /frontend/src/components/Signup.jsx
import { useState } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "../styles/signup.css";

export default function Signup() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const [msg, setMsg] = useState("");
  const [msgType, setMsgType] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const getPasswordStrength = () => {
    if (!password) return { strength: 0, label: "" };
    let strength = 0;
    if (password.length >= 6) strength++;
    if (password.length >= 10) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    
    const labels = ["", "Weak", "Fair", "Good", "Strong", "Unbreakable"];
    return { strength, label: labels[strength] };
  };

  const validate = () => {
    const newErrors = {};
    if (!username.trim()) newErrors.username = "Username is required";
    else if (username.length < 3) newErrors.username = "Username must be at least 3 characters";
    
    if (!password) newErrors.password = "Password is required";
    else if (password.length < 6) newErrors.password = "Password must be at least 6 characters";
    
    if (!confirmPassword) newErrors.confirmPassword = "Please confirm your password";
    else if (password !== confirmPassword) newErrors.confirmPassword = "Passwords do not match";
    
    return newErrors;
  };

  const handleSignup = async () => {
    const newErrors = validate();
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
    
    setErrors({});
    setIsLoading(true);
    setMsg("");
    
    try {
      const res = await axios.post("http://localhost:7000/signup", {
        username,
        password,
      });
      
      // Auto-login after successful signup
      login({ username }, res.data.token);
      
      setMsgType("success");
      setMsg("SUCCESS: Account created! Redirecting...");
      
      // Redirect to dashboard after successful signup
      setTimeout(() => {
        navigate("/dashboard");
      }, 1500);
      
    } catch (err) {
      setMsgType("error");
      setMsg(err.response?.data?.error || "Unknown error");
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !isLoading) {
      handleSignup();
    }
  };

  const passwordStrength = getPasswordStrength();

  const switchToLogin = () => {
    navigate('/login');
  };

  return (
    <div className="auth-card signup-card">
      <div className="card-glow"></div>
      
      <div className="crown-icon">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
          <path d="M12 2L15 8L21 9L16.5 14L18 21L12 17L6 21L7.5 14L3 9L9 8L12 2Z" />
        </svg>
      </div>
      
      <h1>Join the Game</h1>
      <p className="subtitle">Claim your piece on the board</p>

      <div className="input-group">
        <input
          className={`input-box ${errors.username ? "error" : ""}`}
          type="text"
          placeholder="Choose your username"
          value={username}
          onChange={(e) => {
            setUsername(e.target.value);
            setErrors(prev => ({...prev, username: ""}));
          }}
          onKeyPress={handleKeyPress}
          disabled={isLoading}
        />
        {errors.username && <span className="error-text">{errors.username}</span>}
      </div>

      <div className="input-group">
        <div className="password-wrapper">
          <input
            className={`input-box ${errors.password ? "error" : ""}`}
            type={showPassword ? "text" : "password"}
            placeholder="Create password"
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              setErrors(prev => ({...prev, password: ""}));
            }}
            onKeyPress={handleKeyPress}
            disabled={isLoading}
          />
          <button
            type="button"
            className="password-toggle"
            onClick={() => setShowPassword(!showPassword)}
            disabled={isLoading}
          >
            {showPassword ? (
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                <line x1="1" y1="1" x2="23" y2="23" />
              </svg>
            ) : (
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
            )}
          </button>
        </div>
        {password && (
          <div className="password-strength">
            <div className="strength-bar">
              <div 
                className={`strength-fill strength-${passwordStrength.strength}`}
                style={{width: `${(passwordStrength.strength / 5) * 100}%`}}
              ></div>
            </div>
            {passwordStrength.label && (
              <span className="strength-label">{passwordStrength.label}</span>
            )}
          </div>
        )}
        {errors.password && <span className="error-text">{errors.password}</span>}
      </div>

      <div className="input-group">
        <div className="password-wrapper">
          <input
            className={`input-box ${errors.confirmPassword ? "error" : ""}`}
            type={showConfirmPassword ? "text" : "password"}
            placeholder="Confirm password"
            value={confirmPassword}
            onChange={(e) => {
              setConfirmPassword(e.target.value);
              setErrors(prev => ({...prev, confirmPassword: ""}));
            }}
            onKeyPress={handleKeyPress}
            disabled={isLoading}
          />
          <button
            type="button"
            className="password-toggle"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            disabled={isLoading}
          >
            {showConfirmPassword ? (
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                <line x1="1" y1="1" x2="23" y2="23" />
              </svg>
            ) : (
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
            )}
          </button>
        </div>
        {errors.confirmPassword && <span className="error-text">{errors.confirmPassword}</span>}
      </div>

      <button 
        className="primary-btn" 
        onClick={handleSignup}
        disabled={isLoading}
      >
        {isLoading ? (
          <>
            <svg className="spinner" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 12a9 9 0 1 1-6.219-8.56" />
            </svg>
            <span>Creating...</span>
          </>
        ) : (
          "Claim Your Piece"
        )}
      </button>

      <div className="divider">
        <span>or</span>
      </div>

      <button 
        className="secondary-btn" 
        onClick={switchToLogin}
        disabled={isLoading}
      >
        Return to Login
      </button>

      {msg && (
        <p className={`message ${msgType}`}>
          <span className="message-content">{msg}</span>
        </p>
      )}
    </div>
  );
}