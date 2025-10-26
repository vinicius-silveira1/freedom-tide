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

  // Função para mapear tipos de navio para nomes elegantes
  const getShipDisplayName = (shipType) => {
    switch(shipType) {
      case 'MERCHANT_SHIP': return 'Navio Mercante';
      case 'PIRATE_VESSEL': return 'Embarcação Pirata';
      case 'NAVY_PATROL': return 'Patrulha Naval';
      case 'MYSTERIOUS_WRECK': return 'Naufrágio Misterioso';
      default: return 'Embarcação Desconhecida';
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
        name: 'Bordada Completa',
        description: 'Concentrar fogo de todos os canhões contra o casco',
        type: 'ATTACK',
        target: 'HULL',
        icon: 'FOGO'
      },
      {
        id: 'aimed_shot_cannons',
        name: 'Fogo de Fuzil',
        description: 'Neutralizar artilheiros inimigos no convés',
        type: 'ATTACK',
        target: 'CANNONS',
        icon: 'FUZIL'
      },
      {
        id: 'defensive_maneuver',
        name: 'Manobra Evasiva',
        description: 'Navegar para minimizar exposição ao fogo inimigo',
        type: 'DEFENSE',
        icon: 'MANOBRA'
      }
    ];

    // Adicionar ações condicionais baseadas no estado
    if (playerShip && playerShip.repairParts > 0) {
      baseActions.push({
        id: 'emergency_repair',
        name: 'Reparo de Emergência',
        description: `Carpinteiros fazem reparos (${playerShip.repairParts} peças)`,
        type: 'REPAIR',
        icon: 'REPARO'
      });
    }

    if (playerShip && playerShip.shot > 5) {
      baseActions.push({
        id: 'concentrated_fire',
        name: 'Salva Devastadora',
        description: 'Descarregar toda munição disponível (+5 Shot)',
        type: 'SPECIAL_ATTACK',
        icon: 'SALVA'
      });
    }

    // Ação de cura médica - verificar se há tripulação com medicina
    const totalMedicine = Array.isArray(playerShip?.crew) 
      ? playerShip.crew.reduce((sum, member) => sum + member.medicine, 0)
      : 0;
    
    if (totalMedicine > 0 && playerShip?.hull < (playerShip?.maxHull || 100)) {
      baseActions.push({
        id: 'medical_care',
        name: 'Cuidados Médicos',
        description: `Cirurgiões do navio tratam a tripulação (${totalMedicine} medicina)`,
        type: 'HEAL',
        icon: 'MÉDICO'
      });
    }

    // Ação de fuga sempre disponível (mas com consequências)
    baseActions.push({
      id: 'attempt_flee',
      name: 'Bater em Retirada',
      description: 'Tentar escapar do combate naval',
      type: 'FLEE',
      icon: 'FUGA'
    });

    return baseActions;
  };

  // Executar ação de combate com feedback detalhado
  const handleCombatAction = async (action) => {
    setIsAnimating(true);
    
    // Feedback inicial baseado no tipo de ação
    const initialMessages = {
      'ATTACK': `${action.icon} Artilheiros preparam ${action.name.toLowerCase()}...`,
      'HEAL': `${action.icon} Cirurgiões atendem os feridos...`,
      'REPAIR': `${action.icon} Carpinteiros fazem reparos emergenciais...`,
      'DEFENSE': `${action.icon} Executando manobra evasiva...`,
      'SPECIAL_ATTACK': `${action.icon} Preparando ${action.name.toLowerCase()}...`,
      'FLEE': `${action.icon} Tentando escapar do combate...`
    };
    
    setActionFeedback(initialMessages[action.type] || `${action.icon} Executando ${action.name}...`);

    try {
      // Capturar estado anterior para calcular diferenças
      const previousEnemyHull = enemyEncounter?.hull || 0;
      const previousPlayerHull = playerShip?.hull || playerShip?.hullIntegrity || 0;
      
      // Executar ação
      const result = await onCombatAction(action.type, action.target);
      
      // Feedback detalhado baseado no resultado
      let detailedFeedback = '';
      
      if (action.type === 'ATTACK' || action.type === 'SPECIAL_ATTACK') {
        detailedFeedback = `${action.name} executada! Dano causado ao inimigo.`;
      } else if (action.type === 'HEAL') {
        detailedFeedback = `Cuidados médicos aplicados! Tripulação recebe tratamento.`;
      } else if (action.type === 'DEFENSE') {
        detailedFeedback = `Manobra defensiva executada! Reduzindo exposição ao fogo inimigo.`;
      } else if (action.type === 'REPAIR') {
        detailedFeedback = `Reparos de emergência concluídos! Estrutura do navio reforçada.`;
      } else if (action.type === 'FLEE') {
        detailedFeedback = `Tentativa de fuga! Navegando para fora do alcance inimigo.`;
      }
      
      setActionFeedback(detailedFeedback);
      
    } catch (error) {
      setActionFeedback(`Falha na operação: ${error.message}`);
      console.error('Erro na ação de combate:', error);
    } finally {
      setTimeout(() => {
        setIsAnimating(false);
      }, 2000); // Tempo maior para ler o feedback
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
      {/* Relatório de Combate Naval */}
      <div className="combat-narrative">
        <div className="naval-report-content">
          {battlePhase === 'PREPARATION' && (
            <div>
              <h4>📋 RELATÓRIO NAVAL</h4>
              <p>
                <strong>BANDEIRA INIMIGA AVISTADA!</strong><br/>
                {enemyEncounter?.description}
              </p>
              <div className="battle-stats">
                <p><strong>Posição:</strong> Distância próxima</p>
                <p><strong>Status:</strong> Preparando para combate</p>
              </div>
            </div>
          )}
          
          {battlePhase === 'COMBAT' && (
            <div>
              <h4>⚔️ REGISTRO DE COMBATE</h4>
              {actionFeedback && (
                <p className={`action-feedback ${isAnimating ? 'animating' : ''}`}>
                  {actionFeedback}
                </p>
              )}
              <div className="combat-details">
                <p><strong>Casco Nosso:</strong> {playerShip?.hull}/{playerShip?.maxHull || 100}</p>
                <p><strong>Casco Inimigo:</strong> {enemyEncounter?.hull}/{enemyEncounter?.maxHull || getDefaultMaxHull(enemyEncounter?.type)}</p>
                <p><strong>Tripulação:</strong> {playerShip?.crew?.length || 0} homens</p>
              </div>
            </div>
          )}

          {battlePhase === 'RESOLUTION' && (
            <div>
              <h4>🏆 RESOLUÇÃO</h4>
              <p>
                <strong>COMBATE NAVAL RESOLVIDO!</strong><br/>
                Avaliando danos e perdas...
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Mapa de Batalha Naval na Mesa do Capitão */}
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
              <div className={`smoke smoke-level-${playerShipVisual.smokeLvl}`}>FUMAÇA</div>
            )}
          </div>

          {/* Versus Indicator */}
          <div className="versus-indicator">
            <div className="versus-text">CONTRA</div>
            <div className="battle-distance">Distância: Próxima</div>
          </div>

          {/* Navio Inimigo */}
          {enemyEncounter && (
            <div className={enemyShipVisual.className}>
              <div className="ship-hull">
                <div className="ship-icon">
                  {enemyEncounter.type === 'MERCHANT_SHIP' ? '⛵' : 
                   enemyEncounter.type === 'PIRATE_VESSEL' ? '🏴‍☠️' : 
                   enemyEncounter.type === 'NAVY_PATROL' ? '⚓' : 
                   enemyEncounter.type === 'MYSTERIOUS_WRECK' ? '🚢' : '⛵'}
                </div>
                <div className="ship-name">{getShipDisplayName(enemyEncounter.type)}</div>
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
                <div className={`smoke smoke-level-${enemyShipVisual.smokeLvl}`}>FUMAÇA</div>
              )}
            </div>
          )}
        </div>

        {/* Efeitos de Animação */}
        {isAnimating && (
          <div className="battle-effects">
            <div className="cannonfire">FOGO!</div>
            <div className="splash">IMPACTO!</div>
          </div>
        )}
      </div>

      {/* Status da Tripulação durante Combate */}
      <div className="combat-crew-status">
        <h4>Registro de Serviço - Tripulação:</h4>
        <div className="crew-combat-grid">
          <div className="combat-stat">
            <span className="stat-icon">CANHÕES</span>
            <span className="stat-name">Artilharia</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.artillery, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 2.5)}
            </span>
          </div>
          <div className="combat-stat">
            <span className="stat-icon">SABRES</span>
            <span className="stat-name">Combate</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.combat, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 3)}
            </span>
          </div>
          <div className="combat-stat">
            <span className="stat-icon">MADEIRA</span>
            <span className="stat-name">Carpintaria</span>
            <span className="stat-value">
              {Array.isArray(playerShip?.crew) 
                ? playerShip.crew.reduce((sum, member) => sum + member.carpentry, 0) 
                : Math.floor((playerShip?.crew?.crewCount || 0) * 2)}
            </span>
          </div>
          <div className="combat-stat">
            <span className="stat-icon">CIRURGIA</span>
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
          <h3>Ordens de Combate:</h3>
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