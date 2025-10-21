import React from 'react';
import './PortView.css';

const PortView = ({ actions, onActionClick }) => {
  // A mapping from actionType to a display name and maybe a position/style
  const actionDetails = {
    GO_TO_TAVERN: { name: 'Taverna', className: 'tavern-link' },
    GO_TO_MARKET: { name: 'Mercado', className: 'market-link' },
    GO_TO_SHIPYARD: { name: 'Estaleiro', className: 'shipyard-link' },
    VIEW_CONTRACTS: { name: 'Contratos', className: 'contracts-link' },
    TRAVEL: { name: 'Viajar', className: 'travel-link' },
  };

  return (
    <div className="port-view-container">
      {actions.map(action => {
        const details = actionDetails[action.actionType];
        if (!details) {
          // Don't render actions we don't have visual representation for yet
          return null; 
        }
        return (
          <button
            key={action.actionType}
            className={`port-action-link ${details.className}`}
            onClick={() => onActionClick(action)}
          >
            {details.name}
          </button>
        );
      })}
    </div>
  );
};

export default PortView;
