import React from 'react';
import './GameOverScreen.css';

function GameOverScreen({ game, eventLog, onNewGame }) {
  return (
    <div className="game-over-screen">
      <div className="game-over-content">
        <div className="death-banner">
          <h1>🌊 NAUFRÁGIO 🌊</h1>
          <h2>O mar reclama mais uma alma...</h2>
        </div>

        <div className="final-stats">
          <h3>Estatísticas Finais</h3>
          <div className="stats-grid">
            <div className="stat-item">
              <span className="stat-label">Ouro Acumulado:</span>
              <span className="stat-value">{game?.ship?.gold || 0} moedas</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Reputação:</span>
              <span className="stat-value">{game?.captainCompass?.reputation || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Infâmia:</span>
              <span className="stat-value">{game?.captainCompass?.infamy || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Aliança:</span>
              <span className="stat-value">{game?.captainCompass?.alliance || 0}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Tripulação Perdida:</span>
              <span className="stat-value">{game?.crew?.crewCount || 0} almas</span>
            </div>
          </div>
        </div>

        <div className="death-reason">
          <h3>Causa da Queda</h3>
          <p>{game?.gameOverReason || "Destino desconhecido"}</p>
        </div>

        <div className="final-log">
          <h3>Últimos Momentos</h3>
          <div className="final-events">
            {eventLog && eventLog.slice(-5).map((event, index) => (
              <p key={index} className="final-event">{event}</p>
            ))}
          </div>
        </div>

        <div className="game-over-actions">
          <button onClick={onNewGame} className="new-game-button">
            ⚓ Tentar Novamente ⚓
          </button>
        </div>

        <div className="epitaph">
          <p><em>"Aqui jaz um capitão que desafiou as águas do destino...</em></p>
          <p><em>O mar nunca perdoa, mas sempre oferece uma segunda chance."</em></p>
        </div>
      </div>
    </div>
  );
}

export default GameOverScreen;