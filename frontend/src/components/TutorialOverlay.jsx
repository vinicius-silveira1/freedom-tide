import React, { useState, useEffect } from 'react';
import './TutorialOverlay.css';

function TutorialOverlay({ gameId, onTutorialAction, refreshTrigger, gameState }) {
  const [tutorialState, setTutorialState] = useState(null);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isMinimized, setIsMinimized] = useState(false);
  const [isCompleting, setIsCompleting] = useState(false);
  
  // Função para fechar manualmente a tela de conclusão
  const handleCloseTutorialCompletion = () => {
    setIsCompleting(false);
    setTutorialState({ inTutorial: false });
  };

  // Buscar estado atual do tutorial
  const fetchTutorialState = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`/api/games/${gameId}/tutorial`);
      if (!response.ok) {
        throw new Error(`Erro ao buscar tutorial: ${response.status}`);
      }
      const data = await response.json();
      
      // Detectar se o tutorial foi completado
      if (tutorialState?.inTutorial && !data.inTutorial) {
        setIsCompleting(true);
        // Mostrar mensagem por 8 segundos antes de desaparecer automaticamente
        setTimeout(() => {
          setIsCompleting(false);
          setTutorialState(data);
        }, 8000);
      } else {
        setTutorialState(data);
      }
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error('Erro ao buscar tutorial:', e);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (gameId) {
      fetchTutorialState();
    }
  }, [gameId]);

  // Reagir ao refreshTrigger para forçar atualização
  useEffect(() => {
    if (gameId && refreshTrigger > 0) {
      fetchTutorialState();
    }
  }, [refreshTrigger]);

  // Executar ação do tutorial
  const handleTutorialAction = (action) => {
    // Para ações de encontro marítimo, executar a ação real
    if (['ATTACK', 'FLEE', 'BOARD'].includes(action)) {
      // Encontrar a ação correspondente no encontro atual
      const encounterActions = gameState?.currentEncounter?.availableActions || [];
      const matchingAction = encounterActions.find(a => a.actionType === action);
      if (matchingAction && onTutorialAction) {
        onTutorialAction(action, tutorialState, matchingAction);
      }
    } else {
      // Para outras ações (navegação), apenas notificar
      if (onTutorialAction) {
        onTutorialAction(action, tutorialState);
      }
    }
  };

  // Mostrar tela de conclusão se o tutorial foi completado
  if (isCompleting) {
    return (
      <div className="tutorial-overlay tutorial-completion">
        <div className="tutorial-content">
          <div className="tutorial-header">
            <div className="tutorial-title">
              <span className="tutorial-icon">🎉</span>
              <span>Tutorial Completo!</span>
            </div>
          </div>
          <div className="tutorial-progress">
            <div className="tutorial-progress-bar" style={{ width: '100%' }}></div>
          </div>
          <div className="tutorial-message">
            <h3>🏴‍☠️ Parabéns, Capitão!</h3>
            <p>Você completou o tutorial básico do Freedom Tide. Agora está pronto para navegar pelos mares da liberdade e escrever sua própria lenda!</p>
            <ul>
              <li>✅ Tripulação recrutada</li>
              <li>✅ Navio reparado</li>
              <li>✅ Suprimentos estocados</li>
              <li>✅ Primeira viagem realizada</li>
              <li>✅ Encontro marítimo resolvido</li>
            </ul>
            <div className="tutorial-completion-actions">
              <button 
                onClick={handleCloseTutorialCompletion}
                className="tutorial-button tutorial-close-button"
              >
                Continuar Jornada ⚓
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Se não está em tutorial, não renderizar nada
  if (isLoading || !tutorialState || !tutorialState.inTutorial) {
    return null;
  }

  if (error) {
    return (
      <div className="tutorial-overlay tutorial-error">
        <div className="tutorial-content">
          <h3>Erro no Tutorial</h3>
          <p>{error}</p>
          <button onClick={fetchTutorialState} className="tutorial-button">
            Tentar Novamente
          </button>
        </div>
      </div>
    );
  }

  const getPhaseIcon = (phase) => {
    switch (phase) {
      case 'PREPARATION_CREW': return '👥';
      case 'PREPARATION_SHIPYARD': return '🔧';
      case 'PREPARATION_MARKET': return '🛒';
      case 'JOURNEY_START': return '⛵';
      case 'JOURNEY_EVENT': return '⚔️';
      default: return '📋';
    }
  };

  const getProgressPercentage = () => {
    const phases = ['PREPARATION_CREW', 'PREPARATION_SHIPYARD', 'PREPARATION_MARKET', 'JOURNEY_START', 'JOURNEY_EVENT'];
    const currentIndex = phases.indexOf(tutorialState.currentPhase);
    return ((currentIndex + 1) / phases.length) * 100;
  };

  return (
    <div className={`tutorial-overlay ${isMinimized ? 'minimized' : ''}`}>
      <div className="tutorial-content">
        {/* Header com controles */}
        <div className="tutorial-header">
          <div className="tutorial-title">
            <span className="tutorial-icon">{getPhaseIcon(tutorialState.currentPhase)}</span>
            <span>{tutorialState.title}</span>
          </div>
          <div className="tutorial-controls">
            <button 
              onClick={() => setIsMinimized(!isMinimized)} 
              className="tutorial-minimize"
              title={isMinimized ? "Expandir tutorial" : "Minimizar tutorial"}
            >
              {isMinimized ? '▲' : '▼'}
            </button>
          </div>
        </div>

        {/* Barra de progresso */}
        <div className="tutorial-progress">
          <div 
            className="tutorial-progress-bar" 
            style={{ width: `${getProgressPercentage()}%` }}
          />
        </div>

        {!isMinimized && (
          <>
            {/* Mensagem principal */}
            <div className="tutorial-message">
              <p>{tutorialState.message}</p>
            </div>

            {/* Objetivos */}
            {tutorialState.objectives && tutorialState.objectives.length > 0 && (
              <div className="tutorial-objectives">
                <h4>Objetivos:</h4>
                <ul>
                  {tutorialState.objectives.map((objective, index) => (
                    <li key={index}>{objective}</li>
                  ))}
                </ul>
              </div>
            )}

            {/* Checklist de progresso */}
            {tutorialState.checklist && (
              <div className="tutorial-checklist">
                <h4>Progresso:</h4>
                <div className="checklist-items">
                  <div className={`checklist-item ${tutorialState.checklist.crewHired ? 'completed' : ''}`}>
                    <span className="checklist-icon">{tutorialState.checklist.crewHired ? '✅' : '⏳'}</span>
                    <span>Tripulação Contratada</span>
                  </div>
                  <div className={`checklist-item ${tutorialState.checklist.shipRepaired ? 'completed' : ''}`}>
                    <span className="checklist-icon">{tutorialState.checklist.shipRepaired ? '✅' : '⏳'}</span>
                    <span>Navio Reparado</span>
                  </div>
                  <div className={`checklist-item ${tutorialState.checklist.suppliesBought ? 'completed' : ''}`}>
                    <span className="checklist-icon">{tutorialState.checklist.suppliesBought ? '✅' : '⏳'}</span>
                    <span>Suprimentos Estocados</span>
                  </div>
                </div>
              </div>
            )}

            {/* Dicas contextuais */}
            {tutorialState.hints && tutorialState.hints.length > 0 && (
              <div className="tutorial-hints">
                <h4>💡 Dicas:</h4>
                <ul>
                  {tutorialState.hints.map((hint, index) => (
                    <li key={index}>{hint}</li>
                  ))}
                </ul>
              </div>
            )}

            {/* Ações sugeridas */}
            {tutorialState.highlightedActions && tutorialState.highlightedActions.length > 0 && (
              <div className="tutorial-actions">
                <h4>Próximos Passos:</h4>
                <div className="action-buttons">
                  {tutorialState.highlightedActions.map((action, index) => (
                    <button
                      key={index}
                      onClick={() => handleTutorialAction(action)}
                      className="tutorial-action-button"
                    >
                      {action === 'TAVERN' && '🍺 Ir à Taverna'}
                      {action === 'SHIPYARD' && '🔧 Ir ao Estaleiro'}
                      {action === 'MARKET' && '🛒 Ir ao Mercado'}
                      {action === 'TRAVEL' && '⛵ Viajar'}
                      {!['TAVERN', 'SHIPYARD', 'MARKET', 'TRAVEL'].includes(action) && action}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default TutorialOverlay;