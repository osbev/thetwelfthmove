-- Database: thetwelfthmove
CREATE DATABASE IF NOT EXISTS thetwelfthmove;
USE thetwelfthmove;

-- Table: players
CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,   -- store hashed passwords
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample player (passwords should be hashed in actual usage)
INSERT INTO players (username, password) VALUES 
('erica', '$2a$12$eW5l9kI9FJp1vNn7w2k0cO6q8U1vN6G5jIY9bQqD4Yb1iJ0v7RZ6'); -- "123456" hashed