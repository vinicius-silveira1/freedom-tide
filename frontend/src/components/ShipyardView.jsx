import React, { useState, useEffect, useCallback } from 'react';
import './ShipyardView.css';
import '../styles/ancient-documents.css';

function ShipyardView({ gameId, onRepair, onPurchaseUpgrade, onBack }) {
  const [shipyard, setShipyard] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchShipyardData = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/games/${gameId}/port/shipyard`);
      if (!response.ok) {
        throw new Error(`Erro ao buscar dados do estaleiro: ${response.status}`);
      }
      const data = await response.json();
      setShipyard(data);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [gameId]);

  useEffect(() => {
    fetchShipyardData();
  }, [fetchShipyardData]);

  const handleRepair = async () => {
    await onRepair();
    fetchShipyardData(); // Re-fetch para atualizar a UI
  };

  const handlePurchase = async (upgradeId) => {
    await onPurchaseUpgrade(upgradeId);
    fetchShipyardData(); // Re-fetch para atualizar a UI
  };

  return (
    <div className="shipyard-view ancient-document">
      <div className="shipyard-header">
        <div className="shipyard-title">
          <div className="shipyard-decoration ancient-hammer"></div>
          <h2>Estaleiro "Carvalho & Casco"</h2>
          <div className="shipyard-decoration ancient-gear"></div>
        </div>
        <button onClick={onBack} className="action-button back-button authentic-anchor">Voltar ao Porto</button>
      </div>

      {loading && <p>O mestre do estaleiro está inspecionando seu navio...</p>}
      {error && <p className="error-message">{error}</p>}

      {shipyard && (
        <div className="shipyard-content">
          <div className="repair-section">
            <h3>Reparos do Casco</h3>
            <p>{shipyard.message}</p>
            <p>Integridade: {shipyard.hullIntegrity} / {shipyard.maxHullIntegrity}</p>
            {shipyard.repairCost > 0 && (
              <div className="repair-action">
                <p>Custo do Reparo: {shipyard.repairCost} Ouro</p>
                <button onClick={handleRepair} className="action-button repair-button authentic-anchor">
                  Reparar Navio
                </button>
              </div>
            )}
          </div>

          <div className="upgrades-section">
            <h3>Melhorias Disponíveis</h3>
            {shipyard.availableUpgrades.length === 0 ? (
              <p>Nenhuma melhoria disponível para seu navio neste porto.</p>
            ) : (
              <div className="upgrades-list">
                {shipyard.availableUpgrades.map(upgrade => (
                  <div key={upgrade.id} className="upgrade-card small-tag">
                    <h4>{upgrade.name}</h4>
                    <p>{upgrade.description}</p>
                    <p>Efeito: +{upgrade.modifier} {upgrade.type}</p>
                    <div className="upgrade-action">
                      <span>Custo: {upgrade.cost} Ouro</span>
                      <button onClick={() => handlePurchase(upgrade.id)} className="action-button hire-button authentic-anchor">
                        Comprar
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default ShipyardView;
