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

-- Table: games
CREATE TABLE IF NOT EXISTS games (
    game_id INT AUTO_INCREMENT PRIMARY KEY,
    player1_id INT NOT NULL,
    player2_id INT, -- nullable for AI
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    duration INT, -- duration in seconds
    result VARCHAR(20), -- 'player1', 'player2', 'draw', etc.
    status VARCHAR(20) DEFAULT 'ongoing', -- ongoing, completed, abandoned
    CONSTRAINT fk_player1 FOREIGN KEY (player1_id) REFERENCES players(id),
    CONSTRAINT fk_player2 FOREIGN KEY (player2_id) REFERENCES players(id)
);

INSERT INTO games (player1_id, player2_id, status, start_time)
VALUES (?, ?, 'ongoing', NOW());
-- return generated game_id

-- Table: moves
CREATE TABLE IF NOT EXISTS moves (
    move_id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    player_id INT NOT NULL,
    move_number INT NOT NULL,
    from_square VARCHAR(2) NOT NULL,
    to_square VARCHAR(2) NOT NULL,
    piece_moved VARCHAR(20) NOT NULL,
    captured_piece VARCHAR(20),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES games(game_id),
    CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES players(id)
);
