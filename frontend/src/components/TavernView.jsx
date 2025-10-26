import React, { useState, useEffect, useCallback } from 'react';
import './TavernView.css';
import '../styles/ancient-documents.css';
import Tooltip from './Tooltip';

function TavernView({ gameId, onHire, onBack }) {
  const [tavernInfo, setTavernInfo] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  // Descrições das personalidades dos tripulantes
  const personalityDescriptions = {
    HONEST: {
      title: "Honesto",
      description: "Tripulantes honestos têm moral mais estável e são menos afetados por ações questionáveis. Ideais para manter a disciplina a bordo."
    },
    GREEDY: {
      title: "Ganancioso", 
      description: "Gananciosos são motivados por dinheiro. Ficam felizes com bons pagamentos, mas descontentes quando os lucros são baixos."
    },
    BLOODTHIRSTY: {
      title: "Sedento por Sangue",
      description: "Adoram combate e conflito. Ganham moral em batalhas, mas ficam inquietos durante períodos de paz."
    },
    REBEL: {
      title: "Rebelde",
      description: "Questionam autoridade e são independentes. Podem causar problemas, mas são valiosos em situações que exigem pensamento fora da caixa."
    }
  };

  const fetchTavernInfo = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/games/${gameId}/port/tavern`);
      if (!response.ok) {
        throw new Error(`Erro ao buscar informações da taverna: ${response.status}`);
      }
      const data = await response.json();
      setTavernInfo(data);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [gameId]);

  useEffect(() => {
    fetchTavernInfo();
  }, [fetchTavernInfo]);

  const handleHire = async (recruit) => {
    await onHire(recruit.recruitRequest);
    // Após a contratação, atualiza as informações da taverna para feedback imediato
    fetchTavernInfo();
  };

  return (
    <div className="tavern-view ancient-document">
      <div className="tavern-header">
        <div className="tavern-title">
          <div className="tavern-decoration ancient-mug"></div>
          <h2>Taverna "O Grogue Espumante"</h2>
          <div className="tavern-decoration ancient-crossed-swords"></div>
        </div>
        <button onClick={onBack} className="action-button back-button authentic-anchor">Voltar ao Porto</button>
      </div>

      {loading && <p>Procurando por novos rostos na multidão...</p>}
      {error && <p className="error-message">{error}</p>}

      {!loading && !error && tavernInfo && (
        <div className="crew-capacity-info small-tag">
          <h3>Capacidade da Tripulação: {tavernInfo.currentCrewSize}/{tavernInfo.maxCrewCapacity}</h3>
          <p className={tavernInfo.canRecruitMore ? "capacity-good" : "capacity-full"}>
            {tavernInfo.tavernMessage}
          </p>
        </div>
      )}

      {!loading && !error && tavernInfo && (
        <div className="recruits-list">
          {tavernInfo.availableRecruits.map((recruit, index) => (
            <div key={index} className="recruit-card small-tag">
              <div className="recruit-header">
                <h3>{recruit.name}</h3>
                <div className="recruit-profession" style={{ color: recruit.professionColor }}>
                  <img 
                    src={`/assets/icons/professions/${recruit.professionIcon}`} 
                    alt={recruit.profession}
                    className="profession-icon"
                    onError={(e) => {
                      // Fallback para texto se a imagem não carregar
                      e.target.style.display = 'none';
                      e.target.nextSibling.textContent = `[${recruit.profession}] ${recruit.profession}`;
                    }}
                  />
                  <span className="profession-name">{recruit.profession}</span>
                </div>
                <Tooltip 
                  content={personalityDescriptions[recruit.personality]?.description || recruit.personality}
                  className="personality-tooltip"
                >
                  <p className={`personality ${recruit.personality.toLowerCase()}`}>
                    {personalityDescriptions[recruit.personality]?.title || recruit.personality}
                  </p>
                </Tooltip>
              </div>
              <div className="recruit-body">
                <div className="recruit-background">
                  <p className="background-text">"{recruit.background}"</p>
                  <p className="catchphrase" style={{ fontStyle: 'italic', color: recruit.professionColor }}>
                    {recruit.catchphrase}
                  </p>
                  <p className="specialization">
                    <strong>{recruit.specialization}</strong> - {recruit.professionDescription}
                  </p>
                </div>
                <div className="recruit-stats">
                  <p><strong>Custo de Contratação: {recruit.hiringCost} Ouro</strong></p>
                  <p>Salário Mensal: {recruit.salary} Ouro</p>
                  <p>Moral Inicial: {recruit.initialMoral}%</p>
                  <p>Desespero: {recruit.despairLevel}</p>
                  <ul>
                    <Tooltip content="Navegação: Habilidade para guiar o navio e escapar de perigos"><li>NAV: {recruit.navigation}</li></Tooltip>
                    <Tooltip content="Artilharia: Precisão e poder de fogo com canhões"><li>ART: {recruit.artillery}</li></Tooltip>
                    <Tooltip content="Combate: Força e habilidade em batalhas corpo a corpo"><li>CMB: {recruit.combat}</li></Tooltip>
                    <Tooltip content="Medicina: Capacidade de curar ferimentos e doenças"><li>MED: {recruit.medicine}</li></Tooltip>
                    <Tooltip content="Carpintaria: Habilidade para reparar e manter o navio"><li>CAR: {recruit.carpentry}</li></Tooltip>
                    <Tooltip content="Inteligência: Estratégia, conhecimento e resolução de problemas"><li>INT: {recruit.intelligence}</li></Tooltip>
                  </ul>
                </div>
              </div>
              
              <div className="recruit-actions">
                <button 
                  onClick={() => handleHire(recruit)} 
                  className="action-button hire-button authentic-anchor"
                  disabled={!tavernInfo.canRecruitMore}
                  title={!tavernInfo.canRecruitMore ? "Tripulação lotada! Evolva a habilidade Liderança do capitão." : ""}
                >
                    {tavernInfo.canRecruitMore ? "Contratar" : "Tripulação Lotada"}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default TavernView;
