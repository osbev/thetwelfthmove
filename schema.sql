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
('erica', '$2a$12$eW5l9kI9FJp1vNn7w2k0cO6q8U1vN6G5jIY9bQqD4Yb1iJ0v7RZ6') -- "123456" hashed
ON DUPLICATE KEY UPDATE username=username;

-- Table: games
CREATE TABLE IF NOT EXISTS games (
    game_id INT AUTO_INCREMENT PRIMARY KEY,
    player1_id INT NOT NULL,
    player2_id INT, -- nullable for future AI or guest
    current_turn VARCHAR(10) DEFAULT 'white', -- 'white' or 'black'
    board_state TEXT NOT NULL, -- JSON representation of the board
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    duration INT, -- duration in seconds
    winner_id INT, -- NULL for draw or ongoing
    status VARCHAR(20) DEFAULT 'ongoing', -- 'ongoing', 'completed', 'draw', 'abandoned'
    result VARCHAR(20), -- 'white_wins', 'black_wins', 'draw', 'checkmate', 'stalemate'
    CONSTRAINT fk_player1 FOREIGN KEY (player1_id) REFERENCES players(id),
    CONSTRAINT fk_player2 FOREIGN KEY (player2_id) REFERENCES players(id),
    CONSTRAINT fk_winner FOREIGN KEY (winner_id) REFERENCES players(id)
);

-- Table: moves
CREATE TABLE IF NOT EXISTS moves (
    move_id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    player_id INT NOT NULL,
    move_number INT NOT NULL,
    from_square VARCHAR(2) NOT NULL, -- e.g., 'e2'
    to_square VARCHAR(2) NOT NULL,   -- e.g., 'e4'
    piece_type VARCHAR(20) NOT NULL, -- 'pawn', 'knight', 'bishop', 'rook', 'queen', 'king'
    piece_color VARCHAR(10) NOT NULL, -- 'white' or 'black'
    captured_piece VARCHAR(20), -- piece that was captured, if any
    is_check BOOLEAN DEFAULT FALSE,
    is_checkmate BOOLEAN DEFAULT FALSE,
    move_notation VARCHAR(10), -- algebraic notation like 'e4', 'Nf3', 'O-O'
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE,
    CONSTRAINT fk_move_player FOREIGN KEY (player_id) REFERENCES players(id)
);

-- Index for faster queries
CREATE INDEX idx_game_status ON games(status);
CREATE INDEX idx_game_player ON games(player1_id, player2_id);
CREATE INDEX idx_moves_game ON moves(game_id);