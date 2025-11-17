import { useState } from 'react'
import './App.css'

function App() {
  const [board, setBoard] = useState(initializeBoard());

  function initializeBoard() {
    const board = [];
    for (let row = 0; row < 8; row++) {
      board[row] = [];
      for (let col = 0; col < 8; col++) {
        board[row][col] = null;
      }
    }
    // Initialize pieces (simplified)
    // White
    board[0][0] = '♜'; board[0][1] = '♞'; board[0][2] = '♝'; board[0][3] = '♛'; board[0][4] = '♚'; board[0][5] = '♝'; board[0][6] = '♞'; board[0][7] = '♜';
    for (let col = 0; col < 8; col++) board[1][col] = '♟';
    // Black
    board[7][0] = '♖'; board[7][1] = '♘'; board[7][2] = '♗'; board[7][3] = '♕'; board[7][4] = '♔'; board[7][5] = '♗'; board[7][6] = '♘'; board[7][7] = '♖';
    for (let col = 0; col < 8; col++) board[6][col] = '♙';
    return board;
  }

  return (
    <div className="chess-board">
      {board.map((row, rowIndex) => (
        <div key={rowIndex} className="row">
          {row.map((piece, colIndex) => (
            <div key={colIndex} className={`square ${(rowIndex + colIndex) % 2 === 0 ? 'light' : 'dark'}`}>
              {piece}
            </div>
          ))}
        </div>
      ))}
    </div>
  )
}

export default App
