import React from 'react';
import './MenuView.css';

const MenuView = ({ onNewGame }) => {
  return (
    <div className="menu-view-container">
      <div className="title-container">
        <h1 className="game-title">Freedom Tide</h1>
        <p className="game-subtitle">Seu Legado Aguarda</p>
      </div>
      <div className="menu-options">
        <button className="menu-button" onClick={onNewGame}>
          Novo Jogo
        </button>
        {/* Placeholder for future buttons */}
        <button className="menu-button disabled" disabled>
          Continuar (em breve)
        </button>
        <button className="menu-button disabled" disabled>
          Opções (em breve)
        </button>
      </div>
    </div>
  );
};

export default MenuView;
