import React from 'react';
import './CrewStatus.css';

function CrewStatus({ crew }) {
  if (!crew) return <div>Carregando Status da Tripulação...</div>;

  return (
    <div className="status-panel">
      <h2>Tripulação</h2>
      <p>Total de Tripulantes: {crew.crewCount}</p>
      <p>Moral Média: {crew.averageMorale}%</p>
    </div>
  );
}

export default CrewStatus;
