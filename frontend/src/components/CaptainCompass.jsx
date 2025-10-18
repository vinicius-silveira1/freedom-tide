import React from 'react';

function CaptainCompass({ compass }) {
  if (!compass) return <div>Carregando Bússola...</div>;

  return (
    <div className="status-panel">
      <h2>Bússola do Capitão</h2>
      <p>Reputação: {compass.reputation}</p>
      <p>Infâmia: {compass.infamy}</p>
      <p>Aliança: {compass.alliance}</p>
    </div>
  );
}

export default CaptainCompass;
