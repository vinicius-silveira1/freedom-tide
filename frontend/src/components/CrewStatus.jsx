import React from 'react';
import './CrewStatus.css';
import './CrewManagementButton.css';

function CrewStatus({ crew, onViewDetails }) {
  if (!crew) return <div>Carregando Status da Tripulação...</div>;

  return (
    <div className="status-panel">
      <h2>Tripulação</h2>
      <p>Total de Tripulantes: {crew.crewCount}</p>
      <p>Moral Média: {crew.averageMorale}%</p>
      {onViewDetails && (
        <button 
          onClick={onViewDetails} 
          className="action-button crew-details-button"
          title="Ver detalhes de XP, ranks e progressão da tripulação"
        >
          Gerenciar Tripulação
        </button>
      )}
    </div>
  );
}

export default CrewStatus;
