import React, { useState, useEffect } from 'react';
import './NarrativeEncounter.css';

const NarrativeEncounter = ({ 
  encounter, 
  onActionClick, 
  seaNightBackground 
}) => {
  const [isLoading, setIsLoading] = useState(true);

  // Inicializar a cena
  useEffect(() => {
    if (encounter) {
      setTimeout(() => {
        setIsLoading(false);
      }, 1000);
    }
  }, [encounter]);

  // Verificação de segurança
  if (!encounter) {
    return (
      <div className="narrative-encounter error">
        <p>Erro: Encontro não encontrado</p>
      </div>
    );
  }

  // Mapear tipos de encontro para ícones e títulos
  const getEncounterInfo = (type) => {
    switch (type) {
      case 'MYSTERIOUS_WRECK':
        return {
          icon: '/assets/icons/game-over/skull.png',
          title: 'Destroços Misteriosos',
          atmosphere: 'Ventos sussurram entre os destroços à deriva...'
        };
      case 'TRADE_DISPUTE':
        return {
          icon: '/assets/icons/game-over/skull.png',
          title: 'Disputa Comercial',
          atmosphere: 'Vozes alteradas ecoam sobre as águas calmas...'
        };
      case 'MERCHANT_DISTRESS':
        return {
          icon: '/assets/icons/game-over/skull.png',
          title: 'Mercador em Apuros',
          atmosphere: 'Sinais de socorro cortam o silêncio do mar...'
        };
      case 'IMPERIAL_OPPRESSION':
        return {
          icon: '/assets/icons/game-over/skull.png',
          title: 'Opressão Imperial',
          atmosphere: 'O peso da injustiça paira sobre as ondas...'
        };
      case 'UNDERGROUND_NETWORK':
        return {
          icon: '/assets/icons/game-over/skull.png',
          title: 'Rede Clandestina',
          atmosphere: 'Sussurros conspirativos flutuam na brisa marinha...'
        };
      default:
        return {
          icon: '🌊',
          title: 'Encontro Marítimo',
          atmosphere: 'Algo emerge das brumas do oceano...'
        };
    }
  };

  const encounterInfo = getEncounterInfo(encounter.type);

  return (
    <div 
      className="narrative-encounter"
      style={{ backgroundImage: `url(${seaNightBackground})` }}
    >
      {/* Painel de Narrativa */}
      <div className="narrative-panel">
        <div className="narrative-header">
          <div className="encounter-icon">
            {encounterInfo.icon.startsWith('/') ? (
              <img 
                src={encounterInfo.icon} 
                alt="Ícone do Encontro" 
                className="encounter-icon-img"
              />
            ) : (
              encounterInfo.icon
            )}
          </div>
          <h2 className="encounter-title">{encounterInfo.title}</h2>
          <p className="encounter-atmosphere">{encounterInfo.atmosphere}</p>
        </div>

        <div className="narrative-content">
          {isLoading ? (
            <div className="loading-narrative">
              <p className="loading-text">Aproximando-se do encontro...</p>
              <div className="loading-dots">
                <span>.</span>
                <span>.</span>
                <span>.</span>
              </div>
            </div>
          ) : (
            <>
              <div className="encounter-description">
                <p className="description-text">
                  {encounter.description}
                </p>
              </div>

              {/* Ações Disponíveis */}
              {encounter.availableActions && encounter.availableActions.length > 0 && (
                <div className="encounter-actions">
                  <h3>O que fazer?</h3>
                  <div className="actions-list">
                    {encounter.availableActions.map((action) => (
                      <button
                        key={action.actionType}
                        className={`narrative-action-btn ${action.actionType.toLowerCase()}`}
                        onClick={() => onActionClick(action)}
                      >
                        <div className="action-content">
                          <span className="action-icon">
                            {getActionIcon(action.actionType)}
                          </span>
                          <div className="action-text">
                            <span className="action-name">{action.name}</span>
                            <span className="action-description">{action.description}</span>
                          </div>
                        </div>
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* Efeitos Atmosféricos */}
      <div className="atmospheric-effects">
        <div className="mist mist-1"></div>
        <div className="mist mist-2"></div>
        <div className="mist mist-3"></div>
      </div>
    </div>
  );
};

// Função auxiliar para ícones de ação
const getActionIcon = (actionType) => {
  switch (actionType) {
    case 'INVESTIGATE':
      return '🔍';
    case 'NEGOTIATE':
      return '🤝';
    case 'FLEE':
      return '💨';
    case 'BOARD':
      return '⚔️';
    default:
      return '❓';
  }
};

export default NarrativeEncounter;