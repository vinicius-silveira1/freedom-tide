import React, { useState, useEffect, useCallback } from 'react';
import './CrewManagementView.css';

function CrewManagementView({ gameId, onBack }) {
  const [crewData, setCrewData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchCrewData = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/games/${gameId}/crew`);
      if (!response.ok) {
        throw new Error(`Erro ao buscar dados da tripulação: ${response.status}`);
      }
      const data = await response.json();
      setCrewData(data);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [gameId]);

  useEffect(() => {
    fetchCrewData();
  }, [fetchCrewData]);

  const renderXPBar = (currentXP, xpForCurrentRank, xpForNextRank, xpProgress) => {
    const percentage = Math.round(xpProgress * 100);
    
    return (
      <div className="xp-bar-container">
        <div className="xp-bar">
          <div 
            className="xp-bar-fill" 
            style={{ width: `${percentage}%` }}
          ></div>
        </div>
        <div className="xp-text">
          {currentXP} / {xpForNextRank} XP ({percentage}%)
        </div>
      </div>
    );
  };

  if (loading) return <div className="crew-management status-panel">Carregando informações da tripulação...</div>;
  if (error) return <div className="crew-management status-panel error-message">{error}</div>;
  if (!crewData) return <div className="crew-management status-panel">Nenhum dado encontrado</div>;

  return (
    <div className="crew-management status-panel">
      <div className="crew-management-header">
        <h2>Gerenciamento da Tripulação</h2>
        <button onClick={onBack} className="action-button back-button">
          Voltar aos Aposentos
        </button>
      </div>

      {/* Estatísticas Gerais */}
      <div className="crew-statistics">
        <h3>Estatísticas Gerais</h3>
        <div className="stats-grid">
          <div className="stat-item">
            <span className="stat-label">Total de Tripulantes:</span>
            <span className="stat-value">{crewData.totalCrewMembers}</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">Moral Médio:</span>
            <span className="stat-value">{Math.round(crewData.averageMorale)}%</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">Gastos com Salários:</span>
            <span className="stat-value">{crewData.totalSalaryExpenses} Ouro/mês</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">XP Total Ganho:</span>
            <span className="stat-value">{crewData.totalXPEarned} XP</span>
          </div>
        </div>
      </div>

      {/* Resumo por Profissão */}
      <div className="profession-summaries">
        <h3>Resumo por Profissão</h3>
        <div className="profession-grid">
          {crewData.professionSummaries.map((summary, index) => (
            <div key={index} className="profession-summary" style={{ borderLeftColor: summary.professionColor }}>
              <div className="profession-summary-header">
                <img 
                  src={`/assets/icons/professions/${summary.professionIcon}`} 
                  alt={summary.profession}
                  className="profession-summary-icon"
                />
                <span className="profession-summary-name" style={{ color: summary.professionColor }}>
                  {summary.profession}
                </span>
              </div>
              <div className="profession-summary-stats">
                <div>Membros: {summary.memberCount}</div>
                <div>Nível Médio: {summary.averageRankLevel.toFixed(1)}</div>
                <div>XP Total: {summary.totalXP}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Lista Detalhada da Tripulação */}
      <div className="crew-members-list">
        <h3>Tripulação Detalhada</h3>
        {crewData.crewMembers.map((member) => (
          <div key={member.id} className="crew-member-detail">
            <div className="crew-member-header">
              <div className="crew-member-name-profession">
                <h4>{member.name}</h4>
                <div className="crew-member-profession" style={{ color: member.professionColor }}>
                  <img 
                    src={`/assets/icons/professions/${member.professionIcon}`} 
                    alt={member.profession.displayName}
                    className="crew-member-profession-icon"
                  />
                  <span>{member.profession.displayName} - {member.currentRank.displayName}</span>
                </div>
              </div>
              <div className="crew-member-stats-summary">
                <div className="morale-salary">
                  <span>Moral: {member.morale}%</span>
                  <span>Salário: {member.salary} Ouro</span>
                </div>
              </div>
            </div>

            <div className="crew-member-body">
              {/* Progressão de XP */}
              <div className="xp-section">
                <h5>Progressão de Experiência</h5>
                {renderXPBar(member.currentXP, member.xpForCurrentRank, member.xpForNextRank, member.xpProgress)}
                <div className="rank-info">
                  <span>Rank Atual: <strong>{member.currentRank.displayName}</strong></span>
                  {member.nextRank && (
                    <span>Próximo Rank: <strong>{member.nextRank.displayName}</strong></span>
                  )}
                </div>
              </div>

              {/* Atributos */}
              <div className="attributes-section">
                <h5>Atributos</h5>
                <div className="attributes-grid">
                  <div className="attribute">NAV: {member.navigation}</div>
                  <div className="attribute">ART: {member.artillery}</div>
                  <div className="attribute">CMB: {member.combat}</div>
                  <div className="attribute">MED: {member.medicine}</div>
                  <div className="attribute">CAR: {member.carpentry}</div>
                  <div className="attribute">INT: {member.intelligence}</div>
                </div>
              </div>

              {/* Experiência de Campo */}
              <div className="experience-section">
                <h5>Experiência de Campo</h5>
                <div className="experience-stats">
                  <div>Combates: {member.totalCombats}</div>
                  <div>Reparos: {member.totalRepairs}</div>
                  <div>Navegações: {member.totalNavigations}</div>
                  <div>Contratos: {member.totalContracts}</div>
                </div>
              </div>

              {/* Background */}
              <div className="background-section">
                <h5>História</h5>
                <p className="member-background">"{member.background}"</p>
                <p className="member-catchphrase" style={{ color: member.professionColor }}>
                  {member.catchphrase}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CrewManagementView;