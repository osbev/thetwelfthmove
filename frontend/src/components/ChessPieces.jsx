// /frontend/src/components/ChessPieces.jsx
// Gothic pixel-art style chess pieces

export const ChessPieces = {
  white_pawn: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="13" y="8" width="6" height="2" fill="currentColor"/>
      <rect x="11" y="10" width="10" height="2" fill="currentColor"/>
      <rect x="13" y="12" width="6" height="8" fill="currentColor"/>
      <rect x="11" y="20" width="10" height="2" fill="currentColor"/>
      <rect x="9" y="22" width="14" height="4" fill="currentColor"/>
      <rect x="14" y="5" width="4" height="3" fill="currentColor"/>
    </svg>
  ),
  
  white_knight: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="10" y="6" width="8" height="2" fill="currentColor"/>
      <rect x="8" y="8" width="2" height="4" fill="currentColor"/>
      <rect x="10" y="8" width="10" height="2" fill="currentColor"/>
      <rect x="18" y="10" width="2" height="4" fill="currentColor"/>
      <rect x="12" y="10" width="6" height="8" fill="currentColor"/>
      <rect x="10" y="18" width="10" height="2" fill="currentColor"/>
      <rect x="8" y="20" width="14" height="4" fill="currentColor"/>
      <rect x="14" y="6" width="2" height="2" fill="currentColor"/>
      <rect x="16" y="6" width="2" height="2" fill="currentColor"/>
    </svg>
  ),
  
  white_bishop: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="15" y="4" width="2" height="2" fill="currentColor"/>
      <rect x="13" y="6" width="6" height="2" fill="currentColor"/>
      <rect x="11" y="8" width="10" height="2" fill="currentColor"/>
      <rect x="13" y="10" width="6" height="2" fill="currentColor"/>
      <rect x="12" y="12" width="8" height="8" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
      <rect x="14" y="8" width="4" height="2" fill="#000"/>
    </svg>
  ),
  
  white_rook: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="10" y="6" width="4" height="2" fill="currentColor"/>
      <rect x="18" y="6" width="4" height="2" fill="currentColor"/>
      <rect x="10" y="8" width="12" height="2" fill="currentColor"/>
      <rect x="11" y="10" width="10" height="10" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
    </svg>
  ),
  
  white_queen: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="10" y="4" width="2" height="2" fill="currentColor"/>
      <rect x="14" y="4" width="4" height="2" fill="currentColor"/>
      <rect x="20" y="4" width="2" height="2" fill="currentColor"/>
      <rect x="10" y="6" width="12" height="2" fill="currentColor"/>
      <rect x="11" y="8" width="10" height="2" fill="currentColor"/>
      <rect x="12" y="10" width="8" height="10" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
    </svg>
  ),
  
  white_king: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="15" y="3" width="2" height="3" fill="currentColor"/>
      <rect x="13" y="4" width="6" height="2" fill="currentColor"/>
      <rect x="14" y="6" width="4" height="2" fill="currentColor"/>
      <rect x="13" y="8" width="6" height="2" fill="currentColor"/>
      <rect x="11" y="10" width="10" height="2" fill="currentColor"/>
      <rect x="12" y="12" width="8" height="8" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
    </svg>
  ),
  
  black_pawn: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="13" y="8" width="6" height="2" fill="currentColor"/>
      <rect x="11" y="10" width="10" height="2" fill="currentColor"/>
      <rect x="13" y="12" width="6" height="8" fill="currentColor"/>
      <rect x="11" y="20" width="10" height="2" fill="currentColor"/>
      <rect x="9" y="22" width="14" height="4" fill="currentColor"/>
      <rect x="14" y="5" width="4" height="3" fill="currentColor"/>
      <rect x="12" y="11" width="8" height="2" fill="#666"/>
    </svg>
  ),
  
  black_knight: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="10" y="6" width="8" height="2" fill="currentColor"/>
      <rect x="8" y="8" width="2" height="4" fill="currentColor"/>
      <rect x="10" y="8" width="10" height="2" fill="currentColor"/>
      <rect x="18" y="10" width="2" height="4" fill="currentColor"/>
      <rect x="12" y="10" width="6" height="8" fill="currentColor"/>
      <rect x="10" y="18" width="10" height="2" fill="currentColor"/>
      <rect x="8" y="20" width="14" height="4" fill="currentColor"/>
      <rect x="14" y="6" width="2" height="2" fill="currentColor"/>
      <rect x="16" y="6" width="2" height="2" fill="currentColor"/>
      <rect x="13" y="12" width="4" height="2" fill="#666"/>
    </svg>
  ),
  
  black_bishop: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="15" y="4" width="2" height="2" fill="currentColor"/>
      <rect x="13" y="6" width="6" height="2" fill="currentColor"/>
      <rect x="11" y="8" width="10" height="2" fill="currentColor"/>
      <rect x="13" y="10" width="6" height="2" fill="currentColor"/>
      <rect x="12" y="12" width="8" height="8" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
      <rect x="14" y="8" width="4" height="2" fill="#000"/>
      <rect x="13" y="14" width="6" height="2" fill="#666"/>
    </svg>
  ),
  
  black_rook: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="10" y="6" width="4" height="2" fill="currentColor"/>
      <rect x="18" y="6" width="4" height="2" fill="currentColor"/>
      <rect x="10" y="8" width="12" height="2" fill="currentColor"/>
      <rect x="11" y="10" width="10" height="10" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
      <rect x="12" y="12" width="8" height="2" fill="#666"/>
    </svg>
  ),
  
  black_queen: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="10" y="4" width="2" height="2" fill="currentColor"/>
      <rect x="14" y="4" width="4" height="2" fill="currentColor"/>
      <rect x="20" y="4" width="2" height="2" fill="currentColor"/>
      <rect x="10" y="6" width="12" height="2" fill="currentColor"/>
      <rect x="11" y="8" width="10" height="2" fill="currentColor"/>
      <rect x="12" y="10" width="8" height="10" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
      <rect x="13" y="12" width="6" height="2" fill="#666"/>
    </svg>
  ),
  
  black_king: () => (
    <svg viewBox="0 0 32 32" className="chess-piece">
      <rect x="15" y="3" width="2" height="3" fill="currentColor"/>
      <rect x="13" y="4" width="6" height="2" fill="currentColor"/>
      <rect x="14" y="6" width="4" height="2" fill="currentColor"/>
      <rect x="13" y="8" width="6" height="2" fill="currentColor"/>
      <rect x="11" y="10" width="10" height="2" fill="currentColor"/>
      <rect x="12" y="12" width="8" height="8" fill="currentColor"/>
      <rect x="10" y="20" width="12" height="2" fill="currentColor"/>
      <rect x="8" y="22" width="16" height="4" fill="currentColor"/>
      <rect x="13" y="14" width="6" height="2" fill="#666"/>
    </svg>
  ),
};

export const getPieceComponent = (type, color) => {
  const key = `${color}_${type}`;
  return ChessPieces[key];
};