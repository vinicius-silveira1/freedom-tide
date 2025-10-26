import React, { useState, useEffect } from 'react';
import './MapView.css';
import '../styles/ancient-documents.css';
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

  // Ícones por tipo de porto - Pixel Art
  const getPortIcon = (type) => {
    switch(type) {
      case 'IMPERIAL': return <div className="pixel-fort" style={{transform: 'scale(1.5)'}}></div>;
      case 'GUILD': return <div className="pixel-coin" style={{transform: 'scale(1.2)'}}></div>;
      case 'PIRATE': return <div className="pixel-skull" style={{transform: 'scale(1.3)'}}></div>;
      case 'FREE': return <div className="pixel-anchor" style={{transform: 'scale(2.2)'}}></div>;
      default: return <div className="pixel-anchor"></div>;
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
    <div className="status-panel map-view nautical-chart">
      <div className="map-header">
        {/* Âncoras decorativas autênticas */}
        <div className="anchor-decoration-1"><div className="authentic-anchor"></div></div>
        <div className="anchor-decoration-2"><div className="authentic-anchor"></div></div>
        <div className="anchor-decoration-3"><div className="authentic-anchor"></div></div>
        <div className="anchor-decoration-4"><div className="authentic-anchor"></div></div>
        <div className="anchor-decoration-5"><div className="authentic-anchor"></div></div>
        <div className="anchor-decoration-6"><div className="authentic-anchor"></div></div>
        
        <h2>Carta Náutica do Arquipélago de Alvor</h2>
        <p className="map-subtitle">Selecione seu próximo destino, Capitão</p>
      </div>

      <div className="nautical-map">
        {/* Rosa dos Ventos - PNG Icon */}
        <div className="compass-rose">
          <img src="/assets/icons/compass/compass.png" alt="Compass" style={{width: '64px', height: '64px'}} />
        </div>

        {/* Decorações marítimas antigas */}
        <div className="sea-creature sea-creature-1">
          <div className="ancient-kraken"></div>
        </div>
        <div className="sea-creature sea-creature-2">
          <div className="ancient-leviathan"></div>
        </div>
        <div className="decorative-ship decorative-ship-1">
          <div className="pixel-ship"></div>
        </div>
        
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
              {isSelected && currentPort && portCoordinates[currentPort.name] && (
                <div className="travel-route">
                  <svg className="route-svg" viewBox="0 0 100 100" preserveAspectRatio="none">
                    <path 
                      className="route-line"
                      d={`M${portCoordinates[currentPort.name].x},${portCoordinates[currentPort.name].y} Q${(coords.x + portCoordinates[currentPort.name].x)/2},${(coords.y + portCoordinates[currentPort.name].y)/2} ${coords.x},${coords.y}`}
                      fill="none"
                      stroke="#8B4513"
                      strokeWidth="2"
                      strokeDasharray="8,4"
                    />
                  </svg>
                </div>
              )}
            </div>
          );
        })}

        {/* Informações do porto selecionado/hover */}
        {(selectedDestination || hoveredPort) && (
          <div className="port-info-panel small-tag">
            {(() => {
              const port = selectedDestination || destinations.find(d => d.id === hoveredPort);
              const portInfo = portCoordinates[port?.name];
              const typeNames = {
                'IMPERIAL': 'Porto Imperial',
                'GUILD': 'Porto da Guilda',
                'PIRATE': 'Porto Pirata', 
                'FREE': 'Porto Livre'
              };
              
              return (
                <>
                  <h4>{port?.name}</h4>
                  <p className="port-type">
                    {typeNames[portInfo?.type] || portInfo?.type}
                  </p>
                  
                  {!selectedDestination && hoveredPort && (
                    <p style={{fontSize: '0.9em', color: '#654321', margin: '5px 0'}}>
                      Clique para selecionar destino
                    </p>
                  )}
                  
                  {selectedDestination && (
                    <div className="travel-actions">
                      <button className="confirm-travel-button authentic-anchor" onClick={handleConfirmTravel}>
                        Zarpar para {selectedDestination.name}
                      </button>
                      <button className="deselect-button" onClick={() => setSelectedDestination(null)}>
                        Cancelar Seleção
                      </button>
                    </div>
                  )}
                </>
              );
            })()}
          </div>
        )}
      </div>

      <div className="map-controls">
        <button onClick={handleCancel} className="cancel-button authentic-anchor">
          Fechar Cartas
        </button>
      </div>
    </div>
  );
}

export default MapView;