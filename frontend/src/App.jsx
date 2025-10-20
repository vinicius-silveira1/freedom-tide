import { useState, useEffect } from 'react';
import CaptainCompass from './components/CaptainCompass';
import ShipStatus from './components/ShipStatus';
import CrewStatus from './components/CrewStatus';
import LocationStatus from './components/LocationStatus';
import PortActions from './components/PortActions';
import EncounterActions from './components/EncounterActions';
import TravelPanel from './components/TravelPanel'; // Import new component
import './App.css';

function App() {
  const [game, setGame] = useState(null);
  const [error, setError] = useState(null);
  const [currentView, setCurrentView] = useState('DASHBOARD');

  // Fetch initial game state
  useEffect(() => {
    const createAndFetchGame = async () => {
      try {
        const createResponse = await fetch('/api/games', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
        });

        if (!createResponse.ok) {
          throw new Error(`HTTP error! status: ${createResponse.status}`);
        }

        const newGame = await createResponse.json();
        setGame(newGame);

      } catch (e) {
        setError(e.message);
        console.error("Erro ao criar o jogo:", e);
      }
    };

    createAndFetchGame();
  }, []);

  // Function to execute the actual travel POST request
  const executeTravel = async (destinationId) => {
    try {
      const response = await fetch(`/api/games/${game.id}/travel`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ destinationPortId: destinationId }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) {
          throw new Error(`${errorData.message} (Status: ${response.status})`);
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse.gameStatus); // Correctly set the game state to the nested object
      setCurrentView('DASHBOARD'); // Return to dashboard after traveling
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao viajar:", e);
    }
  };

  // Generic action handler
  const handleAction = (action) => {
    switch (action.actionType) {
      case 'TRAVEL':
        setCurrentView('TRAVEL');
        break;
      // other cases for SHIPYARD, MARKET etc. will go here
      default:
        // For now, keep the old post logic for other buttons
        const postAction = async (endpoint) => {
            try {
                const response = await fetch(endpoint, { 
                    method: 'POST', 
                    headers: { 'Content-Type': 'application/json' },
                });
                if (!response.ok) {
                    const errorData = await response.json().catch(() => null);
                    if (errorData && errorData.message) {
                      throw new Error(`${errorData.message} (Status: ${response.status})`);
                    }
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const updatedGameResponse = await response.json();
                setGame(updatedGameResponse.gameStatus);
                setError(null);
            } catch (e) {
                setError(e.message);
                console.error("Erro ao executar a ação:", e);
            }
        };
        postAction(action.apiEndpoint);
        break;
    }
  };

  const renderMainPanel = () => {
    switch (currentView) {
      case 'TRAVEL':
        return <TravelPanel gameId={game.id} onTravel={executeTravel} onCancel={() => setCurrentView('DASHBOARD')} />;
      case 'DASHBOARD':
      default:
        return (
          <>
            <div className="status-dashboard">
              <LocationStatus port={game.currentPort} encounter={game.currentEncounter} />
              <CaptainCompass compass={game.captainCompass} />
              <ShipStatus ship={game.ship} />
              <CrewStatus crew={game.crew} />
            </div>
            {game.currentPort && (
              <PortActions 
                actions={game.currentPort.availableActions}
                onActionClick={handleAction} 
              />
            )}
            {game.currentEncounter && (
              <EncounterActions 
                actions={game.currentEncounter.availableActions}
                onActionClick={handleAction} 
              />
            )}
          </>
        );
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>Freedom Tide</h1>
      </header>
      <main className="main-content">
        {error && (
          <div className="status-panel error-panel">
            <h2>Ocorreu um erro:</h2>
            <pre>{error}</pre>
          </div>
        )}
        {game ? renderMainPanel() : <p>Criando e carregando o jogo...</p>}
      </main>
    </div>
  );
}

export default App;
