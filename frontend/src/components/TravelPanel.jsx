import React, { useState, useEffect } from 'react';
import './TravelPanel.css';

function TravelPanel({ gameId, onTravel, onCancel }) {
  const [destinations, setDestinations] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDestinations = async () => {
      try {
        const response = await fetch(`/api/games/${gameId}/travel/destinations`);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setDestinations(data);
      } catch (e) {
        setError(e.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDestinations();
  }, [gameId]);

  return (
    <div className="status-panel travel-panel">
      <h2>Choose a Destination</h2>
      {isLoading && <p>Loading destinations...</p>}
      {error && <p className="error-message">Error: {error}</p>}
      <div className="destinations-list">
        {destinations.map(dest => (
          <button key={dest.id} onClick={() => onTravel(dest.id)} className="destination-button">
            {dest.name}
          </button>
        ))}
      </div>
      <button onClick={onCancel} className="cancel-button">Cancel</button>
    </div>
  );
}

export default TravelPanel;
