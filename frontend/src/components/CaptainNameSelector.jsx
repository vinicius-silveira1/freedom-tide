import React, { useState } from 'react';
import './CaptainNameSelector.css';
import '../styles/ancient-documents.css';
import audioService from '../utils/AudioService';

const CaptainNameSelector = ({ onConfirm, onBack }) => {
  const [captainName, setCaptainName] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Validar nome
    if (!captainName.trim()) {
      setError('Vossa assinatura é necessária para validar este documento');
      return;
    }
    
    if (captainName.trim().length < 2) {
      setError('Um nome de capitão deve ter ao menos 2 caracteres');
      return;
    }
    
    if (captainName.trim().length > 50) {
      setError('O pergaminho não comporta nome tão extenso (máximo 50 caracteres)');
      return;
    }

    audioService.playSfx('/audio/sfx/ui_click.wav');
    onConfirm(captainName.trim());
  };

  const handleBack = () => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    onBack();
  };

  const handleInputChange = (e) => {
    setCaptainName(e.target.value);
    setError(''); // Limpar erro quando o usuário digitar
  };

  // Sugestões de nomes para inspirar o jogador
  const nameSuggestions = [
    'Anne Bonny', 'Blackbeard', 'Jack Sparrow', 'Mary Read', 
    'Calico Jack', 'Grace O\'Malley', 'William Kidd', 'Bartholomew Roberts'
  ];

  const handleSuggestionClick = (name) => {
    setCaptainName(name);
    setError('');
  };

  return (
    <div className="captain-name-selector">
      <div className="captain-name-panel military-order">
        <div className="captain-name-header">
          <div className="compass-decoration">
            <img 
              src="/assets/icons/stats/compass.png" 
              alt="Bússola do Capitão" 
              className="compass-icon"
            />
          </div>
          <h1 className="pixel-heading">Carta de Nomeação</h1>
          <p className="subtitle pixel-text">Como sereis conhecido pelos Sete Mares?</p>
        </div>

        <form onSubmit={handleSubmit} className="captain-name-form">
          <div className="input-group">
            <label htmlFor="captainName" className="input-label">
              Nome de Guerra:
            </label>
            <input
              type="text"
              id="captainName"
              value={captainName}
              onChange={handleInputChange}
              className={`captain-name-input ${error ? 'error' : ''}`}
              placeholder="Vossa assinatura neste documento..."
              maxLength={50}
              autoFocus
            />
            {error && <span className="error-message">{error}</span>}
          </div>



          <div className="form-actions">
            <button 
              type="button" 
              className="action-button back-button ancient-button"
              onClick={handleBack}
            >
              Voltar
            </button>
            <button 
              type="submit" 
              className="action-button confirm-button ancient-button"
              disabled={!captainName.trim()}
            >
              Assinar e Zarpar
            </button>
          </div>
        </form>
        
        <div className="flavor-text small-tag">
          <p className="pixel-text"><em>"Que vosso nome seja lembrado nas tavernas e temido nos mares."</em></p>
        </div>
      </div>
    </div>
  );
};

export default CaptainNameSelector;