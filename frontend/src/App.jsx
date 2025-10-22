import { useState, useEffect } from 'react';
import MenuView from './components/MenuView'; // Import new component
import IntroSequence from './components/IntroSequence'; // Import intro sequence
import CaptainCompass from './components/CaptainCompass';
import ShipStatus from './components/ShipStatus';
import CrewStatus from './components/CrewStatus';
import LocationStatus from './components/LocationStatus';
import PortView from './components/PortView';
import EncounterActions from './components/EncounterActions';
import TravelPanel from './components/TravelPanel';
import EventLog from './components/EventLog';
import TavernView from './components/TavernView';
import ShipyardView from './components/ShipyardView';
import MarketView from './components/MarketView';
import ContractsView from './components/ContractsView';
import audioService from './utils/AudioService';
import './App.css';
import './components/Dashboard/Dashboard.css';

// Importar as imagens de fundo
import portBackground from './assets/backgrounds/port.jpg';
import seaBackground from './assets/backgrounds/sea-day.png';

function App() {
  const [gameState, setGameState] = useState('MENU'); // MENU, LOADING, INTRO, PLAYING, ERROR
  const [game, setGame] = useState(null);
  const [eventLog, setEventLog] = useState([]);
  const [error, setError] = useState(null);
  const [currentView, setCurrentView] = useState('DASHBOARD');

  // Handler para completar a sequência introdutória
  const handleIntroComplete = async (selectedChoice) => {
    try {
      // Aplicar a escolha inicial na Bússola do Capitão
      const updateResponse = await fetch(`/api/games/${game.id}/compass`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          choice: selectedChoice
        })
      });

      if (!updateResponse.ok) {
        const errorData = await updateResponse.json().catch(() => null);
        if (errorData && errorData.message) {
          throw new Error(errorData.message);
        }
        throw new Error(`HTTP error! status: ${updateResponse.status}`);
      }

      const response = await updateResponse.json();
      setGame(response.gameStatus);
      setEventLog(response.eventLog || []);
      
      // Fazer uma segunda requisição para garantir que temos o estado completo do jogo
      const gameResponse = await fetch(`/api/games/${game.id}`);
      if (gameResponse.ok) {
        const fullGameStatus = await gameResponse.json();
        setGame(fullGameStatus);
      }
      
      setGameState('PLAYING');
    } catch (e) {
      console.error("Erro ao processar escolha inicial:", e);
      setError(e.message);
      // Em caso de erro, continuar mesmo assim
      setGameState('PLAYING');
    }
  };

  // Handler to start a new game
  const handleNewGame = async () => {
    setGameState('LOADING');
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
      setEventLog(["Bem-vindo a Freedom Tide!"]);
      setGameState('INTRO'); // Move to intro sequence first
    } catch (e) {
      setError(e.message);
      setGameState('ERROR'); // Move to error state
      console.error("Erro ao criar o jogo:", e);
    }
  };

  // The old useEffect is removed. Game creation is now manual.

  // EFFECT: Manage background music based on location
  useEffect(() => {
    if (gameState === 'PLAYING' && game) {
      const newTrack = game.currentPort 
        ? '/audio/music/port_music.mp3' 
        : '/audio/music/sea_music.mp3';
      audioService.playMusic(newTrack);
    }
  }, [game?.currentPort, gameState]);

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
      setGame(updatedGameResponse.gameStatus);
      setEventLog(updatedGameResponse.eventLog);
      setCurrentView('DASHBOARD');
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao viajar:", e);
    }
  };

  const handleHire = async (recruitRequest) => {
    try {
      const response = await fetch(`/api/games/${game.id}/crew/recruit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(recruitRequest),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) {
          throw new Error(`${errorData.message} (Status: ${response.status})`);
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse);
      setEventLog(prevLog => [...prevLog, `Novo tripulante contratado: ${recruitRequest.name}!`]);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao contratar:", e);
    }
  };

  const handleRepair = async () => {
    try {
      const response = await fetch(`/api/games/${game.id}/port/shipyard/repair`, { method: 'POST' });
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) { throw new Error(errorData.message); }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse.gameStatus);
      setEventLog(updatedGameResponse.eventLog);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao reparar o navio:", e);
    }
  };

  const handlePurchaseUpgrade = async (upgradeId) => {
    try {
      const response = await fetch(`/api/games/${game.id}/port/shipyard/upgrades/${upgradeId}`, { method: 'POST' });
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) { throw new Error(errorData.message); }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse.gameStatus);
      setEventLog(updatedGameResponse.eventLog);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao comprar melhoria:", e);
    }
  };

  const handleBuy = async (tradeRequest) => {
    try {
      const response = await fetch(`/api/games/${game.id}/port/market/buy`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(tradeRequest),
      });
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) { throw new Error(errorData.message); }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse.gameStatus);
      setEventLog(prevLog => [...prevLog, `Comprou ${tradeRequest.quantity} de ${tradeRequest.item}.`]);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao comprar item:", e);
    }
  };

  const handleSell = async (tradeRequest) => {
    try {
      const response = await fetch(`/api/games/${game.id}/port/market/sell`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(tradeRequest),
      });
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) { throw new Error(errorData.message); }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse.gameStatus);
      setEventLog(updatedGameResponse.eventLog);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao vender item:", e);
    }
  };

  const handleAcceptContract = async (contractId) => {
    try {
      const response = await fetch(`/api/games/${game.id}/contracts/${contractId}/accept`, { method: 'POST' });
      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) { throw new Error(errorData.message); }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const updatedGame = await response.json();
      setGame(updatedGame);
      setEventLog(prevLog => [...prevLog, `Contrato aceito: ${updatedGame.activeContract.title}`]);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao aceitar o contrato:", e);
    }
  };

  const handleAction = (action) => {
    switch (action.actionType) {
      case 'TRAVEL':
        setCurrentView('TRAVEL');
        break;
      case 'GO_TO_TAVERN':
        setCurrentView('TAVERN');
        break;
      case 'GO_TO_SHIPYARD':
        setCurrentView('SHIPYARD');
        break;
      case 'GO_TO_MARKET':
        setCurrentView('MARKET');
        break;
      case 'VIEW_CONTRACTS':
        setCurrentView('CONTRACTS');
        break;
      default:
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
                setEventLog(updatedGameResponse.eventLog);
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

  const getBackgroundStyle = () => {
    // In menu, loading, or when the desk is visible, the background is handled by child components.
    if (gameState !== 'PLAYING' || !game || (currentView === 'DASHBOARD' && game.currentPort)) {
      return {};
    }
    // Only apply sea background when at sea.
    const backgroundImage = !game.currentPort ? seaBackground : '';
    if (backgroundImage) {
        return { backgroundImage: `url(${backgroundImage})` };
    }
    return {};
  };

  const renderMainPanel = () => {
    switch (currentView) {
      case 'TRAVEL':
        return <TravelPanel gameId={game.id} onTravel={executeTravel} onCancel={() => setCurrentView('DASHBOARD')} />;
      case 'TAVERN':
        return <TavernView gameId={game.id} onHire={handleHire} onBack={() => setCurrentView('DASHBOARD')} />;
      case 'SHIPYARD':
        return <ShipyardView gameId={game.id} onRepair={handleRepair} onPurchaseUpgrade={handlePurchaseUpgrade} onBack={() => setCurrentView('DASHBOARD')} />;
      case 'MARKET':
        return <MarketView gameId={game.id} onBuy={handleBuy} onSell={handleSell} onBack={() => setCurrentView('DASHBOARD')} />;
      case 'CONTRACTS':
        return <ContractsView game={game} onAccept={handleAcceptContract} onBack={() => setCurrentView('DASHBOARD')} />;
      case 'DASHBOARD':
      default:
        // The Captain's Desk metaphor is only for when in a port.
        // If at sea, we might want a different layout or just the event log.
        if (game.currentPort) {
          return (
            <div 
              className="captain-desk-container" 
              style={{ backgroundImage: `url(/assets/backgrounds/wood_texture.png)` }}
            >
              {/* Desk Items (Panels) */}
              <div className="desk-item ship-status-on-desk">
                <ShipStatus ship={game.ship} />
              </div>
              <div className="desk-item crew-status-on-desk">
                <CrewStatus crew={game.crew} />
              </div>
              <div className="desk-item location-status-on-desk">
                <LocationStatus port={game.currentPort} />
              </div>
              <div className="desk-item event-log-on-desk">
                <EventLog logs={eventLog} />
              </div>

              {/* Actions are now part of the PortView, which is also on the desk */}
              <div className="desk-item port-view-on-desk">
                 <PortView 
                    actions={game.currentPort?.availableActions || []}
                    onActionClick={handleAction} 
                  />
              </div>

              {/* Decorative Props */}
              <img src="/assets/props/map.png" alt="Map Prop" className="desk-prop prop-map" />
              <img src="/assets/props/sword.png" alt="Sword Prop" className="desk-prop prop-sword" />
              <img src="/assets/props/coins.png" alt="Coins Prop" className="desk-prop prop-coins" />

            </div>
          );
        } else {
          // Default view when at sea (no desk)
          return (
            <>
              <div className="status-dashboard">
                <LocationStatus encounter={game.currentEncounter} />
                <ShipStatus ship={game.ship} />
                <CrewStatus crew={game.crew} />
              </div>
              <EventLog logs={eventLog} />
              {game.currentEncounter && (
                <EncounterActions 
                  actions={game.currentEncounter.availableActions}
                  onActionClick={handleAction} 
                />
              )}
            </>
          );
        }
    }
  };

  // Main render logic based on gameState
  const renderContent = () => {
    switch (gameState) {
      case 'MENU':
        return <MenuView onNewGame={handleNewGame} />;
      case 'LOADING':
        return <div className="loading-screen"><p>Criando e carregando o jogo...</p></div>;
      case 'INTRO':
        return <IntroSequence onComplete={handleIntroComplete} />;
      case 'PLAYING':
        return (
          <>
            <CaptainCompass compass={game?.captainCompass} />
            <header className="app-header">
              <h1>Freedom Tide</h1>
            </header>
            <main className="main-content">
              {renderMainPanel()}
            </main>
          </>
        );
      case 'ERROR':
        return (
          <div className="status-panel error-panel">
            <h2>Ocorreu um erro:</h2>
            <pre>{error}</pre>
            <button onClick={() => setGameState('MENU')}>Voltar ao Menu</button>
          </div>
        );
      default:
        return <MenuView onNewGame={handleNewGame} />;
    }
  }

  return (
    <div className="app-container" style={getBackgroundStyle()}>
      {renderContent()}
    </div>
  );
}

export default App;