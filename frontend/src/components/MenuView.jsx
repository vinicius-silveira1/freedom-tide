import React from 'react';
import './MenuView.css';
import '../styles/ancient-documents.css';
import audioService from '../utils/AudioService';

const MenuView = ({ onNewGame }) => {
  const handleNewGameClick = () => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    onNewGame();
  };

  return (
    <div className="menu-view-container">
      <div className="title-container ancient-document with-seal">
        <h1 className="game-title">Freedom Tide</h1>
        <p className="game-subtitle pixel-text">Seu Legado Aguarda nas Águas</p>
      </div>
      <div className="menu-options ancient-document">
        <h2 className="pixel-heading">Ordens do Almirantado</h2>
        <button className="menu-button ancient-button" onClick={handleNewGameClick}>
          Zarpar em Nova Aventura
        </button>
        {/* Placeholder for future buttons */}
        <button className="menu-button ancient-button disabled" disabled>
          Continuar Jornada
        </button>
        <button className="menu-button ancient-button disabled" disabled>
          Configurações
        </button>
      </div>
    </div>
  );
};

export default MenuView;
