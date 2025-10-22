import React from 'react';
import './PortView.css';
import audioService from '../utils/AudioService';

const PortView = ({ actions, onActionClick }) => {
  // A mapping from actionType to a display name and maybe a position/style
  const actionDetails = {
    GO_TO_TAVERN: { name: 'Taverna', className: 'tavern-link' },
    GO_TO_MARKET: { name: 'Mercado', className: 'market-link' },
    GO_TO_SHIPYARD: { name: 'Estaleiro', className: 'shipyard-link' },
    VIEW_CONTRACTS: { name: 'Contratos', className: 'contracts-link' },
    TRAVEL: { name: 'Viajar', className: 'travel-link' },
  };

  const handleActionClick = (action) => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    onActionClick(action);
  };

  return (
    <div className="port-view-container">
      {actions && actions.map(action => {
        const details = actionDetails[action.actionType];
        if (!details) {
          // Don't render actions we don't have visual representation for yet
          return null; 
        }
        return (
          <button
            key={action.actionType}
            className={`port-action-link ${details.className}`}
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