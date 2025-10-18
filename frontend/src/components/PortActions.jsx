import React, { useState, useEffect } from 'react';

function PortActions({ gameId, currentPort }) {
  const [actions, setActions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!gameId || !currentPort) return;

    const fetchPortActions = async () => {
      setLoading(true);
      try {
        const response = await fetch(`/api/games/${gameId}/port/actions`);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setActions(data);
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };

    fetchPortActions();
  }, [gameId, currentPort]); // Roda o efeito se gameId ou currentPort mudar

  if (loading) return <div className="status-panel">Carregando ações...</div>;
  if (error) return <div className="status-panel error-panel">Erro ao buscar ações: {error}</div>;

  return (
    <div className="status-panel actions-panel">
      <h2>Ações no Porto</h2>
      <div className="actions-grid">
        {actions.map(action => (
          <button key={action.actionType} className="action-button">
            <span className="action-name">{action.name}</span>
            <span className="action-desc">{action.description}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

export default PortActions;
