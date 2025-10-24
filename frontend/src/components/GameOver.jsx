import React from 'react';
import './GameOver.css';

function GameOver({ gameOverReason, onNewGame, onMainMenu }) {
  return (
    <div className="game-over-screen">
      <div className="game-over-panel">
        <div className="game-over-header">
          <h1>
            <img 
              src="/assets/icons/game-over/pirate_flag.png" 
              alt="Bandeira Pirata" 
              className="header-wave-icon"
              onError={(e) => { e.target.outerHTML = '🌊'; }}
            />
            FIM DE JOGO
            <img 
              src="/assets/icons/game-over/pirate_flag.png" 
              alt="Bandeira Pirata" 
              className="header-wave-icon"
              onError={(e) => { e.target.outerHTML = '🌊'; }}
            />
          </h1>
        </div>
        
        <div className="game-over-content">
          <div className="skull-decoration">
            <img 
              src="/assets/icons/game-over/skull.png" 
              alt="Caveira" 
              className="skull-icon"
            />
          </div>
          
          <div className="game-over-message">
            <h2>Sua Jornada Chegou ao Fim</h2>
            <p className="game-over-reason">
              {gameOverReason || "Seu navio foi perdido nas águas traiçoeiras..."}
            </p>
          </div>
          
          <div className="epitaph">
            <p><em>"O mar não perdoa os imprudentes,</em></p>
            <p><em>mas sempre oferece uma segunda chance</em></p>
            <p><em>àqueles corajosos o suficiente para tentar novamente."</em></p>
          </div>
          
          <div className="game-over-actions">
            <button 
              onClick={onNewGame} 
              className="action-button new-game-button"
            >
              Nova Jornada
            </button>
            <button 
              onClick={onMainMenu} 
              className="action-button main-menu-button"
            >
              Menu Principal
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default GameOver;