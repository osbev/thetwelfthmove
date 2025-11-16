Context / Project Overview:
You are tasked to generate a backend for a multiplayer chess-like game called “The Twelfth Move”. The game will have a player system, game board state, and stats tracking. The backend should be written in Java using Javalin for HTTP routing and MySQL for database persistence. The frontend will be handled separately using React, so the backend must serve JSON APIs with proper CORS enabled.

Project Requirements / Constraints:

Programming Language & Frameworks:

Java 25

Javalin 5.6.1 for HTTP REST APIs

Maven for dependency management

Database:

MySQL database named thetwelfthmove

Tables needed: players, games, moves

Use JDBC for database connection (no ORM needed)

Backend Structure (MVC Recommended):

model → Java classes representing database entities (Player, Game, Move, Piece, Board)

dao → Handles database operations for each model

controller → Exposes REST endpoints using Javalin

util → Helper classes for JSON conversion, alerts, or utility functions

Main entry point: App.java that starts the Javalin server

Endpoints (Minimal Example):

/players → GET list all players, POST create a new player

/games → GET list all games, POST create a new game

/moves → GET moves of a game, POST add a move

Project Structure:

thetwelfthmove/
├── package-lock.json
├── pom.xml
├── project-structure.txt
├── frontend/
│   ├── .gitignore
│   ├── package-lock.json
│   ├── package.json
│   ├── README.md
│   ├── public/
│   │   ├── favicon.ico
│   │   ├── index.html
│   │   ├── logo192.png
│   │   ├── logo512.png
│   │   ├── manifest.json
│   │   └── robots.txt
│   └── src/
│       ├── App.css
│       ├── App.js
│       ├── App.test.js
│       ├── index.css
│       ├── index.js
│       ├── logo.svg
│       ├── reportWebVitals.js
│       └── setupTests.js
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── thetwelfthmove/
│   │   │           ├── App.java
│   │   │           ├── contoller/
│   │   │           │   ├── GameController.java
│   │   │           │   ├── PlayerController.java
│   │   │           │   └── StatsController.java
│   │   │           ├── dao/
│   │   │           │   ├── DatabaseConnection.java
│   │   │           │   └── PlayerDAO.java
│   │   │           ├── model/
│   │   │           │   ├── Board.java
│   │   │           │   ├── Move.java
│   │   │           │   ├── Piece.java
│   │   │           │   └── Player.java
│   │   │           └── util/
│   │   │               ├── AlertUtil.java
│   │   │               └── JsonUtil.java
│   │   └── resources/
│   │       └── database/
│   │           └── schema.sql
│   └── test/
│       └── java/
│           └── com/
│               └── thetwelfthmove/
│                   └── AppTest.java
└── target/
    ├── classes/
    │   ├── com/
    │   │   └── thetwelfthmove/
    │   │       ├── App.class
    │   │       ├── contoller/
    │   │       │   ├── GameController.class
    │   │       │   └── PlayerController.class
    │   │       ├── dao/
    │   │       │   ├── DatabaseConnection.class
    │   │       │   └── PlayerDAO.class
    │   │       ├── model/
    │   │       │   └── Player.class
    │   │       ├── util/
    │   │       └── database/
    │   │           └── schema.sql
    └── test-classes/
        └── com/
            └── thetwelfthmove/
                └── AppTest.class



Database Schema Example:

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


Backend Features to Implement:

Proper database connection pooling

CRUD operations for players, games, and moves

JSON serialization/deserialization for REST endpoints

Basic error handling (e.g., 400 for invalid input, 500 for server errors)

Enable CORS for React frontend communication

Minimal logging (for debugging)

Technical Constraints:

Must be compatible with Maven and Java 25

Dependencies allowed: Javalin, MySQL Connector, Gson (for JSON)

No frontend code required, backend only

Modular code with clear separation of concerns

Instructions to AI:

Generate App.java that starts the Javalin server and registers controllers.

Generate DatabaseConnection.java that connects to MySQL using JDBC.

Generate models: Player, Game, Move with necessary fields and getters/setters.

Generate DAO classes for CRUD operations.

Generate controllers with REST endpoints for /players, /games, /moves.

Ensure JSON request/response handling using Gson.

Make sure all routes return proper HTTP status codes and JSON responses.

Organize the files exactly according to the structure above.

Provide sample Maven pom.xml optimized for this backend with dependencies for Javalin, MySQL Connector, Gson, and JUnit 5.