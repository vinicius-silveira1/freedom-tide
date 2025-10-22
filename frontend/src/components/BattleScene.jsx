import React, { useState, useEffect } from 'react';
import './BattleScene.css';

const BattleScene = ({ 
  combatState, 
  onCombatAction, 
  onExitCombat,
  seaBackground 
}) => {
  const [battlePhase, setBattlePhase] = useState('PREPARATION'); // PREPARATION, COMBAT, RESOLUTION
  const [actionFeedback, setActionFeedback] = useState('');
  const [isAnimating, setIsAnimating] = useState(false);

  // Função auxiliar para obter maxHull padrão baseado no tipo
  const getDefaultMaxHull = (shipType) => {
    switch(shipType) {
      case 'MERCHANT_SHIP': return 80;
      case 'PIRATE_VESSEL': return 120;
      case 'NAVY_PATROL': return 150;
      case 'MYSTERIOUS_WRECK': return 60;
      default: return 100;
    }
  };

  // Extrair dados do combatState
  const playerShip = combatState?.playerShip;
  const enemyEncounter = combatState?.encounter;

  // Debug: Verificar dados da tripulação
  console.log('BattleScene - combatState:', combatState);
  console.log('BattleScene - playerShip:', playerShip);
  console.log('BattleScene - crew:', playerShip?.crew);
  console.log('BattleScene - enemyEncounter:', enemyEncounter);
  console.log('BattleScene - enemy hull:', enemyEncounter?.hull, 'maxHull:', enemyEncounter?.maxHull);
  
  // Debug específico para barra de vida do inimigo
  if (enemyEncounter) {
    const maxHull = enemyEncounter.maxHull || getDefaultMaxHull(enemyEncounter.type);
    const healthPercentage = (enemyEncounter.hull / maxHull) * 100;
    console.log('BattleScene - Enemy Health %:', healthPercentage, 'Current:', enemyEncounter.hull, 'Max:', maxHull);
  }

  // Verificação de segurança - se não há combatState válido, não renderizar
  if (!combatState || !playerShip || !enemyEncounter) {
    return (
      <div className="battle-scene-loading">
        <p>Preparando combate...</p>
      </div>
    );
  }

  // Calcular status de combate
  const getShipDamageLevel = (currentHull, maxHull) => {
    const percentage = (currentHull / maxHull) * 100;
    if (percentage > 75) return 'intact';
    if (percentage > 50) return 'damaged';
    if (percentage > 25) return 'heavily-damaged';
    return 'critical';
  };

  const getEnemyDamageLevel = (currentHull, maxHull) => {
    const percentage = (currentHull / maxHull) * 100;
    if (percentage > 75) return 'intact';
    if (percentage > 50) return 'damaged';
    if (percentage > 25) return 'heavily-damaged';
    return 'critical';
  };

  // Ações de combate disponíveis
  const getCombatActions = () => {
    if (!enemyEncounter || battlePhase !== 'COMBAT') return [];

    const baseActions = [
      {
        id: 'aimed_shot_hull',
        name: 'Mirar no Casco',
        description: 'Ataque focado para máximo dano estrutural',
        type: 'ATTACK',
        target: 'HULL',
        icon: '🎯'
      },
      {
        id: 'aimed_shot_cannons',
        name: 'Mirar nos Canhões',
        description: 'Reduzir capacidade ofensiva inimiga',
        type: 'ATTACK',
        target: 'CANNONS',
        icon: '💥'
      },
      {
        id: 'defensive_maneuver',
        name: 'Manobra Defensiva',
        description: 'Reduzir dano recebido no próximo turno',
        type: 'DEFENSE',
        icon: '🛡️'
      }
    ];

    // Adicionar ações condicionais baseadas no estado
    if (playerShip && playerShip.repairParts > 0) {
      baseActions.push({
        id: 'emergency_repair',
        name: 'Reparo de Emergência',
        description: `Usar peças (${playerShip.repairParts} disponíveis)`,
        type: 'REPAIR',
        icon: '🔧'
      });
    }

    if (playerShip && playerShip.shot > 5) {
      baseActions.push({
        id: 'concentrated_fire',
        name: 'Fogo Concentrado',
        description: 'Usar munição extra (+5 Shot)',
        type: 'SPECIAL_ATTACK',
        icon: '💀'
      });
    }

    // Ação de fuga sempre disponível (mas com consequências)
    baseActions.push({
      id: 'attempt_flee',
      name: 'Tentar Fugir',
      description: 'Risco de dano durante a fuga',
      type: 'FLEE',
      icon: '💨'
    });

    return baseActions;
  };

  // Executar ação de combate
  const handleCombatAction = async (action) => {
    setIsAnimating(true);
    setActionFeedback(`${action.icon} ${action.name}...`);

    try {
      // Usar o callback do parent component para fazer a ação
      await onCombatAction(action.type, action.target);
      setActionFeedback(`${action.name} executado com sucesso!`);
    } catch (error) {
      setActionFeedback(`Erro: ${error.message}`);
      console.error('Erro na ação de combate:', error);
    } finally {
      setTimeout(() => {
        setIsAnimating(false);
      }, 1500);
    }
  };

  // Efeitos visuais baseados no estado do combate
  const getShipVisualState = () => {
    if (!playerShip || !playerShip.hullIntegrity || !playerShip.maxHullIntegrity) {
      return {
        className: 'ship player-ship intact',
        smokeLvl: 0
      };
    }
    
    const damageLevel = getShipDamageLevel(playerShip.hullIntegrity, playerShip.maxHullIntegrity);
    return {
      className: `ship player-ship ${damageLevel}`,
      smokeLvl: damageLevel === 'critical' ? 3 : damageLevel === 'heavily-damaged' ? 2 : 1
    };
  };

  const getEnemyVisualState = () => {
    if (!enemyEncounter) return { className: 'ship enemy-ship intact', smokeLvl: 0 };
    
    // Se não há maxHull definido, usar um valor padrão baseado no tipo de navio
    const maxHull = enemyEncounter.maxHull || getDefaultMaxHull(enemyEncounter.type);
    const damageLevel = getEnemyDamageLevel(enemyEncounter.hull, maxHull);
    return {
      className: `ship enemy-ship ${damageLevel}`,
      smokeLvl: damageLevel === 'critical' ? 3 : damageLevel === 'heavily-damaged' ? 2 : 1
    };
  };

  // Função auxiliar para obter maxHull padrão baseado no tipo
  // Inicializar combate
  useEffect(() => {
    if (enemyEncounter && battlePhase === 'PREPARATION') {
      setTimeout(() => {
        setBattlePhase('COMBAT');
        setActionFeedback('Combate iniciado! Escolha sua ação...');
      }, 1000);
    }
  }, [enemyEncounter, battlePhase]);

  const playerShipVisual = getShipVisualState();
  const enemyShipVisual = getEnemyVisualState();
  const combatActions = getCombatActions();

  return (
    <div 
      className="battle-scene"
      style={{ backgroundImage: `url(${seaBackground})` }}
    >
      {/* Narrativa de Combate */}
      <div className="combat-narrative">
        <div className="narrative-content">
          {battlePhase === 'PREPARATION' && (
            <p>
              <strong>⚡ COMBATE IMINENTE!</strong><br/>
              {enemyEncounter?.description}
            </p>
          )}
          
          {battlePhase === 'COMBAT' && actionFeedback && (
            <p className={`action-feedback ${isAnimating ? 'animating' : ''}`}>
              {actionFeedback}
            </p>
          )}

          {battlePhase === 'RESOLUTION' && (
            <p>
              <strong>🏆 COMBATE RESOLVIDO!</strong><br/>
              Calculando consequências...
            </p>
          )}
        </div>
      </div>

      {/* Arena de Batalha */}
      <div className="battle-arena">
        <div className="battle-waters">
          {/* Navio do Jogador */}
          <div className={playerShipVisual.className}>
            <div className="ship-hull">
              <div className="ship-icon">🚢</div>
              <div className="ship-name">{playerShip?.name || 'Seu Navio'}</div>
              <div className="hull-bar">
                <div 
                  className="hull-fill" 
                  style={{ width: `${playerShip?.hullIntegrity && playerShip?.maxHullIntegrity ? (playerShip.hullIntegrity / playerShip.maxHullIntegrity) * 100 : 0}%` }}
                ></div>
              </div>
              <div className="hull-text">
                {playerShip?.hullIntegrity || 0}/{playerShip?.maxHullIntegrity || 0}
              </div>
            </div>
            {playerShipVisual.smokeLvl > 0 && (
              <div className={`smoke smoke-level-${playerShipVisual.smokeLvl}`}>💨</div>
            )}
          </div>

          {/* Versus Indicator */}
          <div className="versus-indicator">
            <div className="versus-text">⚔️</div>
            <div className="battle-distance">Distância: Próxima</div>
          </div>

          {/* Navio Inimigo */}
          {enemyEncounter && (
            <div className={enemyShipVisual.className}>
              <div className="ship-hull">
                <div className="ship-icon">
                  {enemyEncounter.type === 'MERCHANT_SHIP' ? '🛳️' : 
                   enemyEncounter.type === 'PIRATE_VESSEL' ? '🏴‍☠️' : 
                   enemyEncounter.type === 'NAVY_PATROL' ? '⚓' : '🚢'}
                </div>
                <div className="ship-name">{enemyEncounter.type.replace('_', ' ')}</div>
                <div className="hull-bar enemy">
                  <div 
                    className="hull-fill" 
                    style={{ 
                      width: `${(() => {
                        const maxHull = enemyEncounter.maxHull || getDefaultMaxHull(enemyEncounter.type);
                        return (enemyEncounter.hull / maxHull) * 100;
                      })()}%` 
                    }}
                  ></div>
                </div>
                <div className="hull-text">
                  {enemyEncounter.hull || 0}/{enemyEncounter.maxHull || getDefaultMaxHull(enemyEncounter.type)}
                </div>
              </div>
              {enemyShipVisual.smokeLvl > 0 && (
                <div className={`smoke smoke-level-${enemyShipVisual.smokeLvl}`}>💨</div>
              )}
            </div>
          )}
        </div>

        {/* Efeitos de Animação */}
        {isAnimating && (
          <div className="battle-effects">
            <div className="cannonfire">💥</div>
            <div className="splash">💦</div>
          </div>
        )}
      </div>

      {/* Status da Tripulação durante Combate */}
      <div className="combat-crew-status">
        <h4>Tripulação em Combate:</h4>
        <div className="crew-combat-grid">
          <div className="combat-stat">
            <span className="stat-icon">💥</span>
            <span className="stat-name">Artilharia</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.artillery, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 2.5)}
            </span>
          </div>
          <div className="combat-stat">
            <span className="stat-icon">⚔️</span>
            <span className="stat-name">Combate</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.combat, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 3)}
            </span>
          </div>
          <div className="combat-stat">
            <span className="stat-icon">🔧</span>
            <span className="stat-name">Carpintaria</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.carpentry, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 2)}
            </span>
          </div>
          <div className="combat-stat">
            <span className="stat-icon">💊</span>
            <span className="stat-name">Medicina</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.medicine, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 1.5)}
            </span>
          </div>
        </div>
      </div>

      {/* Ações de Combate */}
      {battlePhase === 'COMBAT' && !isAnimating && (
        <div className="combat-actions">
          <h3>Escolha sua Ação:</h3>
          <div className="actions-grid">
            {combatActions.map(action => (
              <button
                key={action.id}
                className={`combat-action-btn ${action.type.toLowerCase()}`}
                onClick={() => handleCombatAction(action)}
                disabled={isAnimating}
              >
                <span className="action-icon">{action.icon}</span>
                <span className="action-name">{action.name}</span>
                <span className="action-description">{action.description}</span>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default BattleScene;