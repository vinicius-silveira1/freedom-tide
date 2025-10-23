import React, { useState, useEffect, useCallback } from 'react';
import './TavernView.css';

function TavernView({ gameId, onHire, onBack }) {
  const [recruits, setRecruits] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchRecruits = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/games/${gameId}/port/tavern`);
      if (!response.ok) {
        throw new Error(`Erro ao buscar recrutas: ${response.status}`);
      }
      const data = await response.json();
      setRecruits(data);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [gameId]);

  useEffect(() => {
    fetchRecruits();
  }, [fetchRecruits]);

  const handleHire = async (recruit) => {
    await onHire(recruit.recruitRequest);
    // Após a contratação, atualiza a lista de recrutas para feedback imediato
    fetchRecruits();
  };

  return (
    <div className="tavern-view status-panel">
      <div className="tavern-header">
        <h2>Taverna "O Grogue Espumante"</h2>
        <button onClick={onBack} className="action-button back-button">Voltar ao Porto</button>
      </div>

      {loading && <p>Procurando por novos rostos na multidão...</p>}
      {error && <p className="error-message">{error}</p>}

      {!loading && !error && (
        <div className="recruits-list">
          {recruits.map((recruit, index) => (
            <div key={index} className="recruit-card">
              <div className="recruit-header">
                <h3>{recruit.name}</h3>
                <p className={`personality ${recruit.personality.toLowerCase()}`}>{recruit.personality}</p>
              </div>
              <div className="recruit-body">
                <div className="recruit-stats">
                  <p><strong>Custo de Contratação: {recruit.hiringCost} Ouro</strong></p>
                  <p>Salário Mensal: {recruit.salary} Ouro</p>
                  <p>Moral Inicial: {recruit.initialMoral}%</p>
                  <p>Desespero: {recruit.despairLevel}</p>
                  <ul>
                    <li>NAV: {recruit.navigation}</li>
                    <li>ART: {recruit.artillery}</li>
                    <li>CMB: {recruit.combat}</li>
                    <li>MED: {recruit.medicine}</li>
                    <li>CAR: {recruit.carpentry}</li>
                    <li>INT: {recruit.intelligence}</li>
                  </ul>
                </div>
                <div className="recruit-actions">
                    <button onClick={() => handleHire(recruit)} className="action-button hire-button">
                        Contratar
                    </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default TavernView;
