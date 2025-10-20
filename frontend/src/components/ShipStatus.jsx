import React from 'react';
import './ShipStatus.css';

function ShipStatus({ ship }) {
  if (!ship) return <div>Carregando Status do Navio...</div>;

  return (
    <div className="status-panel">
      <h2>Navio: {ship.name} ({ship.type})</h2>
      <p>Integridade do Casco: {ship.hullIntegrity}</p>
      <p>Ouro: {ship.gold}</p>
      <h3>Recursos:</h3>
      <ul>
        <li>Comida: {ship.foodRations}</li>
        <li>Rum: {ship.rumRations}</li>
        <li>Peças de Reparo: {ship.repairParts}</li>
        <li>Munição: {ship.shot}</li>
      </ul>
    </div>
  );
}

export default ShipStatus;
