import React from 'react';
import './LocationStatus.css';

function LocationStatus({ port, encounter }) {
  let location = "Em alto mar";
  let isAtSea = true;
  
  if (port) {
    location = `Atracado em: ${port.name} (${port.type})`;
    isAtSea = false;
  }
  // Durante encontros, manter apenas "Em alto mar"
  // Os detalhes do encontro aparecem no EventLog

  return (
    <div className={`status-panel ${isAtSea ? 'compact-at-sea' : ''}`}>
      <h2>Localização Atual</h2>
      <p>{location}</p>
    </div>
  );
}

export default LocationStatus;
