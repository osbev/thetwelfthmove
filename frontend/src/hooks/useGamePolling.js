// /frontend/src/hooks/useGamePolling.js
import { useEffect, useRef, useState } from 'react';
import axios from 'axios';

export default function useGamePolling(gameId, onUpdate, enabled = true) {
  const [lastMoveCount, setLastMoveCount] = useState(0);
  const [opponentMoved, setOpponentMoved] = useState(false);
  const intervalRef = useRef(null);

  useEffect(() => {
    if (!gameId || !enabled) return;

    const pollGame = async () => {
      try {
        const response = await axios.get(`http://localhost:7000/games/${gameId}/poll`);
        const data = response.data;

        // Check if there's a new move
        if (data.moveCount > lastMoveCount) {
          setLastMoveCount(data.moveCount);
          
          // Only show opponent moved notification if not first poll
          if (lastMoveCount > 0) {
            setOpponentMoved(true);
            setTimeout(() => setOpponentMoved(false), 3000);
          }

          // Call update callback
          if (onUpdate) {
            onUpdate(data);
          }
        }

        // Also update if game status changed (e.g., player joined)
        if (data.game) {
          onUpdate(data);
        }
      } catch (error) {
        console.error('Polling error:', error);
      }
    };

    // Initial poll
    pollGame();

    // Set up interval (every 3 seconds)
    intervalRef.current = setInterval(pollGame, 3000);

    // Cleanup
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [gameId, enabled, lastMoveCount, onUpdate]);

  return { opponentMoved };
}