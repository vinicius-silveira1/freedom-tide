import React from 'react';
import './CaptainCompass.css';
import '../styles/ancient-documents.css';

function CaptainCompass({ compass, captainName }) {
  // Se a bússola não estiver disponível, renderiza um placeholder invisível para manter o espaço
  if (!compass) {
    return <div className="compass-widget" style={{ visibility: 'hidden' }}></div>;
  }

  // Componente simplificado - apenas exibe os valores do capitão

  return (
    <div className="compass-widget small-tag">
      {captainName && (
        <div className="captain-name-display">
          <span className="captain-title">Capitão</span>
          <span className="captain-name">{captainName}</span>
        </div>
      )}
      
      {/* Status do capitão simplificado */}
      <div className="captain-status">
        <div className="status-item reputation">
          <span className="status-label">Reputação</span>
          <span className="status-value">{compass.reputation}</span>
        </div>
        <div className="status-item infamy">
          <span className="status-label">Infâmia</span>
          <span className="status-value">{compass.infamy}</span>
        </div>
        <div className="status-item alliance">
          <span className="status-label">Aliança</span>
          <span className="status-value">{compass.alliance}</span>
        </div>
      </div>
    </div>
  );
}

export default CaptainCompass;
