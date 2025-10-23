import { useState, useEffect } from 'react';
import MenuView from './components/MenuView'; // Import new component
import IntroSequence from './components/IntroSequence'; // Import intro sequence
import GameOver from './components/GameOver'; // Import game over screen
import CaptainCompass from './components/CaptainCompass';
import ShipStatus from './components/ShipStatus';
import CrewStatus from './components/CrewStatus';
import LocationStatus from './components/LocationStatus';
import PortView from './components/PortView';
import EncounterActions from './components/EncounterActions';
import BattleScene from './components/BattleScene'; // New combat component
import MapView from './components/MapView';
import EventLog from './components/EventLog';
import TavernView from './components/TavernView';
import ShipyardView from './components/ShipyardView';
import MarketView from './components/MarketView';
import ContractsView from './components/ContractsView';
import TutorialOverlay from './components/TutorialOverlay';
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
  const [tutorialRefresh, setTutorialRefresh] = useState(0); // Força refresh do tutorial
  const [wasInEncounter, setWasInEncounter] = useState(false); // Rastrear se estava em encontro
  const [inCombat, setInCombat] = useState(false); // Estado de combate ativo
  const [combatState, setCombatState] = useState(null); // Estado específico do combate

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

  // Handler para ações do tutorial
  const handleTutorialAction = async (action, tutorialState, encounterAction = null) => {
    try {
      // Para ações de encontro marítimo, executar a ação real
      if (['ATTACK', 'FLEE', 'BOARD'].includes(action) && encounterAction) {
        await handleAction(encounterAction);
        return;
      }
      
      // Navegar para a view apropriada quando botão é clicado
      switch (action) {
        case 'TAVERN':
          setCurrentView('TAVERN');
          break;
        case 'SHIPYARD':
          setCurrentView('SHIPYARD');
          break;
        case 'MARKET':
          setCurrentView('MARKET');
          break;
        case 'TRAVEL':
          setCurrentView('TRAVEL');
          break;
      }
      
      // Atualizar o estado do jogo após ação do tutorial
      const gameResponse = await fetch(`/api/games/${game.id}`);
      if (gameResponse.ok) {
        const updatedGame = await gameResponse.json();
        setGame(updatedGame);
      }
    } catch (e) {
      console.error("Erro ao atualizar jogo após ação do tutorial:", e);
    }
  };

  // Função para notificar progresso do tutorial
  const notifyTutorialProgress = async (action) => {
    try {
      console.log(`Notificando progresso do tutorial: ${action}`);
      const response = await fetch(`/api/games/${game.id}/tutorial/progress`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action })
      });
      
      if (!response.ok) {
        console.error(`Erro na resposta da API tutorial: ${response.status}`);
        const errorData = await response.json().catch(() => null);
        console.error('Dados do erro:', errorData);
      } else {
        console.log('Progresso do tutorial notificado com sucesso');
      }
      
      // Forçar refresh do tutorial
      setTutorialRefresh(prev => prev + 1);
    } catch (e) {
      console.error("Erro ao notificar progresso do tutorial:", e);
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

  // Helper para verificar game over em qualquer resposta
  const checkGameOver = (gameResponse) => {
    if (gameResponse.gameOver || gameResponse.gameStatus.gameOver) {
      setGameState('GAME_OVER');
      setEventLog(prevLog => [...prevLog, "=== FIM DE JOGO ===", ...(gameResponse.eventLog || [])]);
      return true;
    }
    return false;
  };

  // Handler para reiniciar o jogo completamente (usado no game over)
  const handleRestartGame = async () => {
    // Reset todos os estados
    setGame(null);
    setEventLog([]);
    setError(null);
    setCurrentView('port');
    setCombatState(null);
    setWasInEncounter(false);
    setTutorialRefresh(0);
    
    // Ir para o menu para criar um novo jogo
    setGameState('MENU');
  };

  // Handler para novo jogo após game over
  const handleNewGameFromGameOver = async () => {
    await handleNewGame();
  };

  // Handler para voltar ao menu principal
  const handleMainMenu = () => {
    setGameState('MENU');
    setGame(null);
    setEventLog([]);
    setError(null);
    setCurrentView('port');
    setCombatState(null);
    setWasInEncounter(false);
    setTutorialRefresh(0);
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

  // EFFECT: Track encounter state and detect resolution
  useEffect(() => {
    if (gameState === 'PLAYING' && game) {
      // Rastrear se o jogador está em um encontro
      if (game.currentEncounter && !wasInEncounter) {
        console.log('Jogador entrou em encontro marítimo');
        setWasInEncounter(true);
      }
      
      // Se estava em encontro e agora está em um porto (sem encontro), foi resolvido
      if (wasInEncounter && !game.currentEncounter && game.currentPort) {
        console.log('Detectado: Chegada ao porto após resolução de encontro - completando tutorial');
        setWasInEncounter(false);
        // Notificar que o encontro foi resolvido (completa o tutorial)
        notifyTutorialProgress('ENCOUNTER_RESOLVED');
      }
    }
  }, [game?.currentEncounter, game?.currentPort, wasInEncounter, gameState]);

  // EFFECT: Detect combat start and manage battle scene
  useEffect(() => {
    if (game?.currentEncounter && game.currentEncounter.type) {
      const combatTypes = ['MERCHANT_SHIP', 'PIRATE_VESSEL', 'NAVY_PATROL', 'MYSTERIOUS_WRECK'];
      
      // Só ativar combate se:
      // 1. É um tipo de encontro de combate
      // 2. Não está em combate já
      // 3. O encontro tem ações disponíveis (indicando que é interativo)
      // 4. Não está em processo de viagem (currentPort é null significa em viagem)
      const shouldActivateCombat = combatTypes.includes(game.currentEncounter.type) && 
                                  !inCombat && 
                                  game.currentEncounter.availableActions && 
                                  game.currentEncounter.availableActions.length > 0 &&
                                  !game.currentPort; // Não no porto
      
      if (shouldActivateCombat) {
        console.log('Ativando BattleScene para encontro:', game.currentEncounter.type);
        setInCombat(true);
        setCombatState({
          encounter: game.currentEncounter,
          playerShip: {
            ...game.ship,
            crew: game.crew || []
          },
          turn: 'PLAYER',
          combatLog: [],
          isPlayerTurn: true
        });
      }
    } else if (inCombat && !game?.currentEncounter) {
      // Saiu do combate
      console.log('Desativando BattleScene - sem encontro');
      setInCombat(false);
      setCombatState(null);
    }
  }, [game?.currentEncounter, game?.currentPort, inCombat]);

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
      
      // Verificar se o jogo terminou
      if (checkGameOver(updatedGameResponse)) {
        return;
      }
      
      setCurrentView('DASHBOARD');
      setError(null);
      
      // Notificar progresso do tutorial para viagem
      await notifyTutorialProgress('START_JOURNEY');
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
      
      // Notificar progresso do tutorial
      await notifyTutorialProgress('HIRE_CREW');
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
      
      // Notificar progresso do tutorial
      await notifyTutorialProgress('REPAIR_SHIP');
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
      
      // Notificar progresso do tutorial para compras de suprimentos
      if (tradeRequest.item === 'FOOD' || tradeRequest.item === 'RUM') {
        await notifyTutorialProgress('BUY_SUPPLIES');
      }
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

  // Combat action handlers for BattleScene
  const handleCombatAction = async (actionType, target = null) => {
    try {
      let endpoint = '';
      let requestBody = {};
      
      switch (actionType) {
        case 'ATTACK':
          endpoint = `/api/games/${game.id}/encounter/attack`;
          // Mapear targets do BattleScene para o backend
          const backendTarget = target === 'HULL' ? 'hull' : 
                               target === 'CANNONS' ? 'cannons' : 
                               target === 'SAILS' ? 'sails' : 'hull';
          requestBody = { target: backendTarget };
          break;
        case 'BOARD':
        case 'SPECIAL_ATTACK':
          endpoint = `/api/games/${game.id}/encounter/board`;
          break;
        case 'FLEE':
          endpoint = `/api/games/${game.id}/encounter/flee`;
          break;
        case 'REPAIR':
        case 'DEFENSE':
          // Para ações que não têm endpoint específico, usar attack como fallback
          endpoint = `/api/games/${game.id}/encounter/attack`;
          requestBody = { target: 'hull' };
          break;
        default:
          throw new Error(`Ação de combate desconhecida: ${actionType}`);
      }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        if (errorData && errorData.message) {
          throw new Error(errorData.message);
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const updatedGameResponse = await response.json();
      setGame(updatedGameResponse.gameStatus);
      setEventLog(updatedGameResponse.eventLog);
      
      // Verificar se o jogo terminou
      if (checkGameOver(updatedGameResponse)) {
        return; // Sair da função sem processar mais atualizações
      }
      
      // Atualizar o combatState com nova informação
      if (combatState && updatedGameResponse.gameStatus.currentEncounter) {
        setCombatState(prev => ({
          ...prev,
          encounter: updatedGameResponse.gameStatus.currentEncounter,
          playerShip: {
            ...updatedGameResponse.gameStatus.ship,
            crew: updatedGameResponse.gameStatus.crew || []
          },
          combatLog: [...(prev.combatLog || []), ...updatedGameResponse.eventLog.slice(-1)],
          turn: prev.turn === 'PLAYER' ? 'ENEMY' : 'PLAYER',
          isPlayerTurn: prev.turn !== 'PLAYER'
        }));
      }
      
      setError(null);
      
      // Notificar progresso do tutorial para ações de encontro
      await notifyTutorialProgress('ENCOUNTER_ACTION');
    } catch (e) {
      setError(e.message);
      console.error("Erro na ação de combate:", e);
    }
  };

  const handleExitCombat = () => {
    setInCombat(false);
    setCombatState(null);
  };

  const handleAction = (action) => {
    // Notificar progresso do tutorial para navegação
    const notifyNavigation = async (actionType) => {
      switch (actionType) {
        case 'GO_TO_TAVERN':
          await notifyTutorialProgress('TAVERN');
          break;
        case 'GO_TO_SHIPYARD':
          await notifyTutorialProgress('SHIPYARD');
          break;
        case 'GO_TO_MARKET':
          await notifyTutorialProgress('MARKET');
          break;
        case 'TRAVEL':
          await notifyTutorialProgress('TRAVEL');
          break;
      }
    };
    
    switch (action.actionType) {
      case 'TRAVEL':
        setCurrentView('TRAVEL');
        notifyNavigation('TRAVEL');
        break;
      case 'GO_TO_TAVERN':
        setCurrentView('TAVERN');
        notifyNavigation('GO_TO_TAVERN');
        break;
      case 'GO_TO_SHIPYARD':
        setCurrentView('SHIPYARD');
        notifyNavigation('GO_TO_SHIPYARD');
        break;
      case 'GO_TO_MARKET':
        setCurrentView('MARKET');
        notifyNavigation('GO_TO_MARKET');
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
                const newGameState = updatedGameResponse.gameStatus;
                setGame(newGameState);
                setEventLog(updatedGameResponse.eventLog);
                setError(null);
                
                // Verificar se o jogo terminou
                if (checkGameOver(updatedGameResponse)) {
                    return; // Sair da função sem processar mais atualizações
                }
                
                // Verificar se o encontro foi resolvido após a ação
                if (!newGameState.currentEncounter && newGameState.currentPort) {
                    console.log('Encontro resolvido após ação - completando tutorial');
                    await notifyTutorialProgress('ENCOUNTER_RESOLVED');
                }
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
        return <MapView gameId={game.id} onTravel={executeTravel} onCancel={() => setCurrentView('DASHBOARD')} currentPort={game.currentPort} />;
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
          // View when at sea with full background coverage
          return (
            <div 
              className="sea-encounter-container" 
              style={{ backgroundImage: `url(${seaBackground})` }}
            >
              {inCombat && combatState ? (
                <BattleScene 
                  combatState={combatState}
                  onCombatAction={handleCombatAction}
                  onExitCombat={handleExitCombat}
                  seaBackground={seaBackground}
                />
              ) : (
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
              )}
            </div>
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
      case 'GAME_OVER':
        return (
          <GameOver 
            gameOverReason={game?.gameOverReason}
            onNewGame={handleNewGameFromGameOver}
            onMainMenu={handleMainMenu}
          />
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
      {gameState === 'PLAYING' && game && (
        <TutorialOverlay 
          gameId={game.id} 
          onTutorialAction={handleTutorialAction}
          refreshTrigger={tutorialRefresh}
          gameState={game}
        />
      )}
    </div>
  );
}

export default App;