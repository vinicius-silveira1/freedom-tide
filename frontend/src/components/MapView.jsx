import React, { useState, useEffect } from 'react';
import './MapView.css';
import audioService from '../utils/AudioService';

function MapView({ gameId, onTravel, onCancel, currentPort }) {
  const [destinations, setDestinations] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedDestination, setSelectedDestination] = useState(null);
  const [hoveredPort, setHoveredPort] = useState(null);

  useEffect(() => {
    const fetchDestinations = async () => {
      try {
        const response = await fetch(`/api/games/${gameId}/travel/destinations`);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setDestinations(data);
      } catch (e) {
        setError(e.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDestinations();
  }, [gameId]);

  // Coordenadas dos portos no mapa (percentuais)
  const portCoordinates = {
    // Portos Originais
    'Porto Real': { x: 50, y: 35, type: 'IMPERIAL' },
    'Baía da Guilda': { x: 75, y: 55, type: 'GUILD' },
    'Ninho do Corvo': { x: 25, y: 70, type: 'PIRATE' },
    
    // Região Norte Imperial
    'Fortaleza de Ferro': { x: 45, y: 15, type: 'IMPERIAL' },
    'Porto da Coroa': { x: 55, y: 20, type: 'IMPERIAL' },
    
    // Região Leste da Guilda
    'Entreposto Dourado': { x: 85, y: 45, type: 'GUILD' },
    'Ilha dos Mercadores': { x: 90, y: 65, type: 'GUILD' },
    
    // Região Sul Independente
    'Vila dos Pescadores': { x: 40, y: 85, type: 'FREE' },
    'Ruínas de Atlântida': { x: 60, y: 90, type: 'FREE' },
    
    // Região Oeste Pirata
    'Refúgio do Kraken': { x: 10, y: 60, type: 'PIRATE' }
  };

  // Ícones por tipo de porto
  const getPortIcon = (type) => {
    switch(type) {
      case 'IMPERIAL': return '⚔️';
      case 'GUILD': return '💰';
      case 'PIRATE': return '🏴‍☠️';
      case 'FREE': return '🏛️';
      default: return '⚓';
    }
  };

  const handlePortClick = (destination) => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    
    if (selectedDestination?.id === destination.id) {
      // Confirmar viagem se já estava selecionado
      onTravel(destination.id);
    } else {
      // Selecionar porto
      setSelectedDestination(destination);
    }
  };

  const handleConfirmTravel = () => {
    if (selectedDestination) {
      audioService.playSfx('/audio/sfx/ui_click.wav');
      onTravel(selectedDestination.id);
    }
  };

  const handleCancel = () => {
    audioService.playSfx('/audio/sfx/ui_click.wav');
    onCancel();
  };

  if (isLoading) {
    return (
      <div className="status-panel map-view">
        <h2>Carta Náutica do Arquipélago</h2>
        <p>Desenrolando os mapas de navegação...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="status-panel map-view">
        <h2>Carta Náutica do Arquipélago</h2>
        <p className="error-message">Erro ao carregar cartas: {error}</p>
        <button onClick={handleCancel} className="cancel-button">
          Voltar ao Porto
        </button>
      </div>
    );
  }

  return (
    <div className="status-panel map-view">
      <div className="map-header">
        <h2>Carta Náutica do Arquipélago de Alvor</h2>
        <p className="map-subtitle">Selecione seu próximo destino, Capitão</p>
      </div>

      <div className="nautical-map">
        {/* Rosa dos Ventos */}
        <div className="compass-rose">
          <div className="compass-needle"></div>
          <div className="compass-directions">
            <span className="north">N</span>
            <span className="south">S</span>
            <span className="east">E</span>
            <span className="west">W</span>
          </div>
        </div>

        {/* Decorações marítimas */}
        <div className="sea-monster sea-monster-1">🐙</div>
        <div className="sea-monster sea-monster-2">🐋</div>
        <div className="decorative-ship decorative-ship-1">⛵</div>
        
        {/* Título do mapa */}
        <div className="map-title">
          <h3>MARES DE ALVOR</h3>
          <p>Anno Domini MDCCLX</p>
        </div>

        {/* Portos */}
        {destinations.map(dest => {
          const coords = portCoordinates[dest.name];
          if (!coords) return null;

          const isSelected = selectedDestination?.id === dest.id;
          const isHovered = hoveredPort === dest.id;
          
          return (
            <div key={dest.id}>
              {/* Porto */}
              <div 
                className={`port-marker ${coords.type.toLowerCase()} ${isSelected ? 'selected' : ''} ${isHovered ? 'hovered' : ''}`}
                style={{ 
                  left: `${coords.x}%`, 
                  top: `${coords.y}%` 
                }}
                onClick={() => handlePortClick(dest)}
                onMouseEnter={() => setHoveredPort(dest.id)}
                onMouseLeave={() => setHoveredPort(null)}
              >
                <div className="port-icon">{getPortIcon(coords.type)}</div>
                <div className="port-name">{dest.name}</div>
              </div>

              {/* Rota animada se selecionado */}
              {isSelected && currentPort && (
                <div className="travel-route">
                  <svg className="route-svg" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <path 
                      className="route-line"
                      d={`M${portCoordinates[currentPort.name]?.x || 50},${portCoordinates[currentPort.name]?.y || 50} Q${(coords.x + 50)/2},${(coords.y + 50)/2} ${coords.x},${coords.y}`}
                      fill="none"
                      stroke="#8B4513"
                      strokeWidth="2"
                      strokeDasharray="5,5"
                    />
                  </svg>
                </div>
              )}
            </div>
          );
        })}

        {/* Informações do porto selecionado/hover */}
        {(selectedDestination || hoveredPort) && (
          <div className="port-info-panel">
            <h4>{(selectedDestination || destinations.find(d => d.id === hoveredPort))?.name}</h4>
            <p className="port-type">
              {portCoordinates[(selectedDestination || destinations.find(d => d.id === hoveredPort))?.name]?.type}
            </p>
            
            {selectedDestination && (
              <div className="travel-actions">
                <button className="confirm-travel-button" onClick={handleConfirmTravel}>
                  ⚓ Zarpar para {selectedDestination.name}
                </button>
                <button className="deselect-button" onClick={() => setSelectedDestination(null)}>
                  Cancelar Seleção
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      <div className="map-controls">
        <button onClick={handleCancel} className="cancel-button">
          📜 Fechar Cartas
        </button>
      </div>
    </div>
  );
}

export default MapView;