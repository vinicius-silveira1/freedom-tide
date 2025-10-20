import React from 'react';
import './CaptainCompass.css';

function CaptainCompass({ compass }) {
  // Se a bússola não estiver disponível, renderiza um placeholder invisível para manter o espaço
  if (!compass) {
    return <div className="compass-widget" style={{ visibility: 'hidden' }}></div>;
  }

  // Mapeia valores (0-1000) para graus de rotação
  const reputationDeg = (compass.reputation / 1000) * 100 - 50;
  const infamyDeg = (compass.infamy / 1000) * 80 + 100;
  const allianceDeg = 260 - (compass.alliance / 1000) * 80;

  return (
    <div className="compass-widget">
      <div className="compass-container">
        <div className="compass-rose">
          <div className="compass-center-dial"></div>
        </div>

        {/* Agulhas com valores de título para acessibilidade */}
        <div
          className="compass-needle reputation"
          style={{ transform: `rotate(${reputationDeg}deg)` }}
          title={`Reputação: ${compass.reputation}`}
        ></div>
        <div
          className="compass-needle infamy"
          style={{ transform: `rotate(${infamyDeg}deg)` }}
          title={`Infâmia: ${compass.infamy}`}
        ></div>
        <div
          className="compass-needle alliance"
          style={{ transform: `rotate(${allianceDeg}deg)` }}
          title={`Aliança: ${compass.alliance}`}
        ></div>
      </div>
      
      {/* Leituras numéricas para clareza */}
      <div className="compass-readouts">
        <div className="readout reputation">
          <span className="readout-label">Reputação</span>
          <span className="readout-value">{compass.reputation}</span>
        </div>
        <div className="readout infamy">
          <span className="readout-label">Infâmia</span>
          <span className="readout-value">{compass.infamy}</span>
        </div>
        <div className="readout alliance">
          <span className="readout-label">Aliança</span>
          <span className="readout-value">{compass.alliance}</span>
        </div>
      </div>
    </div>
  );
}

export default CaptainCompass;
