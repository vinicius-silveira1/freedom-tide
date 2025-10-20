import React from 'react';
import './LocationStatus.css';

function LocationStatus({ port, encounter }) {
  let location = "Em alto mar";
  if (port) {
    location = `Atracado em: ${port.name} (${port.type})`;
  } else if (encounter) {
    location = `Encontro no mar: ${encounter.description}`;
  }

  return (
    <div className="status-panel">
      <h2>Localização Atual</h2>
      <p>{location}</p>
    </div>
  );
}

export default LocationStatus;
