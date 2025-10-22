import React from 'react';

function EncounterActions({ actions, onActionClick }) {
  if (!actions || actions.length === 0) {
    return (
      <div className="status-panel actions-panel">
        <h2>Ações de Encontro</h2>
        <p>Nenhuma ação disponível no momento.</p>
      </div>
    );
  }

  return (
    <div className="status-panel actions-panel">
      <h2>Ações de Encontro</h2>
      <div className="actions-grid">
        {actions.map(action => (
          <button
            key={action.actionType}
            className={`action-button ${action.actionType === 'FLEE' ? 'flee-action' : ''}`}
            onClick={() => onActionClick(action)} // Pass the whole action object
          >
            <span className="action-name">{action.name}</span>
            <span className="action-desc">{action.description}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

export default EncounterActions;
