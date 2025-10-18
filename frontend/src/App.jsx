import { useState, useEffect } from 'react';
import CaptainCompass from './components/CaptainCompass';
import ShipStatus from './components/ShipStatus';
import CrewStatus from './components/CrewStatus';
import LocationStatus from './components/LocationStatus';
import PortActions from './components/PortActions';
import './App.css';

function App() {
  const [game, setGame] = useState(null);
  const [error, setError] = useState(null);

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
        setGame(newGame); // Define o estado inicial logo após a criação

      } catch (e) {
        setError(e.message);
        console.error("Erro ao criar o jogo:", e);
      }
    };

    createAndFetchGame();
  }, []);

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
        {game ? (
          <>
            <div className="status-dashboard">
              <LocationStatus port={game.currentPort} encounter={game.currentEncounter} />
              <CaptainCompass compass={game.captainCompass} />
              <ShipStatus ship={game.ship} />
              <CrewStatus crew={game.crew} />
            </div>
            {game.currentPort && (
              <PortActions gameId={game.id} currentPort={game.currentPort} />
            )}
          </>
        ) : (
          <p>Criando e carregando o jogo...</p>
        )}
      </main>
    </div>
  );
}

export default App;
