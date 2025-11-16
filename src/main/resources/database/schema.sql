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

-- Sample players
INSERT INTO players (username, password) VALUES 
('erica', '123'),
('alice', '456');

-- Table: games
CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player1_id INT NOT NULL,
    player2_id INT,                     -- optional for future multiplayer
    winner_id INT,                      -- NULL if ongoing or draw
    status ENUM('ONGOING','FINISHED','DRAW') DEFAULT 'ONGOING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (player1_id) REFERENCES players(id),
    FOREIGN KEY (player2_id) REFERENCES players(id),
    FOREIGN KEY (winner_id) REFERENCES players(id)
);

-- Table: moves
CREATE TABLE IF NOT EXISTS moves (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    player_id INT NOT NULL,
    move_number INT NOT NULL,
    from_position VARCHAR(5) NOT NULL, -- e.g., "A2"
    to_position VARCHAR(5) NOT NULL,   -- e.g., "A4"
    piece_type VARCHAR(20) NOT NULL,   -- e.g., "Pawn"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (player_id) REFERENCES players(id)
);

-- Table: pieces (optional)
CREATE TABLE IF NOT EXISTS pieces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    player_id INT NOT NULL,
    piece_type VARCHAR(20) NOT NULL,
    position VARCHAR(5) NOT NULL,      -- e.g., "A2"
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (game_id) REFERENCES games(id),
    FOREIGN KEY (player_id) REFERENCES players(id)
);

-- Sample game & moves
-- Start a game
INSERT INTO games (player1_id, player2_id) VALUES (1, 2);

-- Add a move
INSERT INTO moves (game_id, player_id, move_number, from_position, to_position, piece_type)
VALUES (1, 1, 1, 'A2', 'A4', 'Pawn');
