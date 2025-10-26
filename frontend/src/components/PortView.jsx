import React from 'react';
import './PortView.css';
import '../styles/ancient-documents.css';
import audioService from '../utils/AudioService';

// Reload com âncoras reais e centralização - 2024-10-25 12:45

const PortView = ({ actions, onActionClick }) => {
  // A mapping from actionType to a display name and maybe a position/style
  const actionDetails = {
    GO_TO_TAVERN: { name: 'Taverna', className: 'tavern-link' },
    GO_TO_MARKET: { name: 'Mercado', className: 'market-link' },
    GO_TO_SHIPYARD: { name: 'Estaleiro', className: 'shipyard-link' },
    VIEW_CONTRACTS: { name: 'Contratos', className: 'contracts-link' },
    TRAVEL: { name: 'Viajar', className: 'travel-link' },
    RESOLVE_CONTRACT: { name: 'Resolver Contrato', className: 'resolve-contract-link' },
    VIEW_CAPTAIN_SKILLS: { name: 'Progressão', className: 'captain-skills-link' },
  };

  const handleActionClick = (action) => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    onActionClick(action);
  };

  return (
    <div className="ancient-document port-manifest">
      <h2>Ações Disponíveis</h2>
      {actions && actions.map(action => {
        const details = actionDetails[action.actionType];
        if (!details) {
          // Don't render actions we don't have visual representation for yet
          return null; 
        }
        return (
          <button
            key={action.actionType}
            className={`ancient-button ${details.className}`}
            onClick={() => handleActionClick(action)}
          >
            {details.name}
          </button>
        );
      })}
    </div>
  );
};

export default PortView;