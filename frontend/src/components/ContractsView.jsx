import { useState, useEffect } from 'react';
import './ContractsView.css';

function ContractsView({ game, onAccept, onBack }) {
  // O contrato ativo agora é derivado diretamente da prop do jogo
  const { activeContract } = game;

  const [contracts, setContracts] = useState([]); // Para os contratos disponíveis
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAvailableContracts = async () => {
      if (!game || !game.id) return;
      try {
        setLoading(true);
        const response = await fetch(`/api/games/${game.id}/contracts`);
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const availableContracts = await response.json(); // A resposta da API é o próprio array
        setContracts(availableContracts); // Define o array para o estado
        setError(null);
      } catch (e) {
        setError(e.message);
        console.error("Erro ao buscar contratos disponíveis:", e);
      } finally {
        setLoading(false);
      }
    };

    fetchAvailableContracts();
  }, [game]); // O efeito é re-executado quando o estado do jogo muda

  if (loading) {
    return <div className="contracts-view">Carregando contratos...</div>;
  }

  if (error) {
    return <div className="contracts-view error">Erro: {error}</div>;
  }

  return (
    <div className="contracts-view status-panel">
      <h2>Quadro de Contratos</h2>

      {activeContract && (
        <div className="active-contract-section">
          <h3>Contrato Ativo</h3>
          <div className="contract-card active">
            <h4>{activeContract.title}</h4>
            <p>{activeContract.description}</p>
            <div className="contract-rewards">
              <span>Ouro: {activeContract.rewardGold}</span>
              <span>Reputação: {activeContract.rewardReputation}</span>
              <span>Infâmia: {activeContract.rewardInfamy}</span>
              <span>Aliança: {activeContract.rewardAlliance}</span>
            </div>
          </div>
        </div>
      )}

      <div className="available-contracts-section">
        <h3>Contratos Disponíveis</h3>
        {contracts && contracts.length > 0 ? (
          contracts.map(contract => (
            <div key={contract.id} className="contract-card">
              <h4>{contract.title}</h4>
              <p>{contract.description}</p>
              <div className="contract-rewards">
                <span>Ouro: {contract.rewardGold}</span>
                <span>Reputação: {contract.rewardReputation}</span>
                <span>Infâmia: {contract.rewardInfamy}</span>
                <span>Aliança: {contract.rewardAlliance}</span>
              </div>
              <button 
                onClick={() => onAccept(contract.id)}
                disabled={!!activeContract}
              >
                Aceitar
              </button>
            </div>
          ))
        ) : (
          <p>Nenhum contrato disponível no momento.</p>
        )}
      </div>

      <button onClick={onBack} className="back-button">Voltar ao Porto</button>
    </div>
  );
}

export default ContractsView;