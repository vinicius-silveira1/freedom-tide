import { useState, useEffect } from 'react';
import CaptainCompass from './components/CaptainCompass';
import ShipStatus from './components/ShipStatus';
import CrewStatus from './components/CrewStatus';
import LocationStatus from './components/LocationStatus';
import PortActions from './components/PortActions';
import EncounterActions from './components/EncounterActions';
import TravelPanel from './components/TravelPanel';
import EventLog from './components/EventLog';
import TavernView from './components/TavernView';
import ShipyardView from './components/ShipyardView';
import MarketView from './components/MarketView';
import ContractsView from './components/ContractsView'; // Importar ContractsView
import './App.css';

function App() {
  const [game, setGame] = useState(null);
  const [eventLog, setEventLog] = useState([]);
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
        setEventLog(["Bem-vindo a Freedom Tide! O seu navio aguarda no porto."]);

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

  // 3. Novas funções para o Mercado
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
      const updatedGame = await response.json(); // A resposta é o próprio objeto de estado do jogo
      setGame(updatedGame);
      setEventLog(prevLog => [...prevLog, `Contrato aceito: ${updatedGame.activeContract.title}`]);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error("Erro ao aceitar o contrato:", e);
    }
  };

  // Generic action handler
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
      case 'VIEW_CONTRACTS': // Adicionar caso para contratos
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
      case 'CONTRACTS': // Adicionar caso de renderização para contratos
        return <ContractsView game={game} onAccept={handleAcceptContract} onBack={() => setCurrentView('DASHBOARD')} />;
      case 'DASHBOARD':
      default:
        return (
          <>
            <div className="status-dashboard">
              <LocationStatus port={game.currentPort} encounter={game.currentEncounter} />
              <ShipStatus ship={game.ship} />
              <CrewStatus crew={game.crew} />
            </div>

            <EventLog logs={eventLog} />

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
      {/* A Bússola agora vive fora do fluxo principal para posicionamento independente */}
      <CaptainCompass compass={game?.captainCompass} />

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
