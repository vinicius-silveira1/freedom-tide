import React, { useState, useEffect } from 'react';
import './CaptainProgression.css';
import '../styles/ancient-documents.css';

const CaptainProgression = ({ gameId, onClose }) => {
  const [captainData, setCaptainData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedTree, setSelectedTree] = useState('COMBAT');

  useEffect(() => {
    fetchCaptainProgression();
  }, [gameId]);

  const fetchCaptainProgression = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/games/${gameId}/captain/progression`);
      if (!response.ok) {
        throw new Error('Erro ao buscar progressão do capitão');
      }
      const data = await response.json();
      console.log('Captain progression data:', data);
      setCaptainData(data);
    } catch (error) {
      console.error('Erro:', error);
    } finally {
      setLoading(false);
    }
  };

  const investSkillPoint = async (skillName) => {
    try {
      const response = await fetch(`/api/games/${gameId}/captain/invest-skill`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ skillName }),
      });
      
      if (!response.ok) {
        throw new Error('Erro ao investir ponto de habilidade');
      }
      
      // Recarregar dados após investimento
      fetchCaptainProgression();
    } catch (error) {
      console.error('Erro ao investir:', error);
      alert('Erro ao investir ponto de habilidade');
    }
  };

  const getSkillsForTree = (tree) => {
    if (!captainData) return [];
    
    const skillsByTree = {
      'COMBAT': [
        captainData.combatProwess,
        captainData.navalTactics,
        captainData.crewInspiration
      ],
      'TRADE': [
        captainData.merchantEye,
        captainData.negotiation,
        captainData.economicMind
      ],
      'EXPLORATION': [
        captainData.seaKnowledge,
        captainData.weatherReading,
        captainData.navigationMaster
      ]
    };
    
    // Filtrar apenas habilidades válidas (não undefined/null)
    const skills = skillsByTree[tree] || [];
    return skills.filter(skill => skill != null);
  };

  const treeOptions = [
    { key: 'COMBAT', name: 'Combate', icon: '' },
    { key: 'TRADE', name: 'Comércio', icon: '' },
    { key: 'EXPLORATION', name: 'Exploração', icon: '' }
  ];

  const renderSkillLevel = (currentLevel, maxLevel = 3) => {
    const dots = [];
    for (let i = 1; i <= maxLevel; i++) {
      dots.push(
        <span 
          key={i} 
          className={`level-dot ${i <= currentLevel ? 'filled' : ''}`}
        >
          ●
        </span>
      );
    }
    return dots;
  };

  const canInvest = (skill) => {
    return skill.currentLevel < 3 && 
           captainData.availableSkillPoints >= skill.costForNextLevel;
  };

  // Calcular progresso XP
  const xpProgress = captainData ? 
    (captainData.currentXP / captainData.xpForNextLevel) * 100 : 0;

  if (loading) {
    return (
      <div className="captain-progression">
        <div className="loading">Carregando progressão do capitão...</div>
      </div>
    );
  }

  if (!captainData) {
    return (
      <div className="captain-progression">
        <div className="error">Erro ao carregar dados do capitão</div>
      </div>
    );
  }

  return (
    <div className="captain-progression military-order">
      <div className="captain-header">
        <div className="progression-title">
          <div className="progression-decoration ancient-medal"></div>
          <h2>Progressão do Capitão</h2>
          <div className="progression-decoration ancient-rank-insignia"></div>
        </div>
        <button className="close-button authentic-anchor" onClick={onClose}>×</button>
      </div>

      {/* Status do Capitão */}
      <div className="captain-status small-tag">
        <div className="level-info">
          <span className="level">Nível {captainData.currentLevel}</span>
          <span className="skill-points">
            {captainData.availableSkillPoints > 0 && (
              <span className="available-points">
                {captainData.availableSkillPoints} pontos disponíveis
              </span>
            )}
          </span>
        </div>

        <div className="xp-bar">
          <div className="xp-fill" style={{ width: `${xpProgress}%` }}></div>
          <span className="xp-text">
            {captainData.currentXP} / {captainData.xpForNextLevel} XP
          </span>
        </div>
      </div>

      {/* Seletor de Árvores */}
      <div className="tree-selector">
        {treeOptions.map(tree => (
          <button
            key={tree.key}
            className={`tree-tab authentic-anchor ${selectedTree === tree.key ? 'active' : ''}`}
            onClick={() => setSelectedTree(tree.key)}
          >
            {tree.name}
          </button>
        ))}
      </div>

      {/* Árvore de Habilidades */}
      <div className="skills-tree">
        <h3>{treeOptions.find(t => t.key === selectedTree)?.name}</h3>
        
        <div className="skills-list">
          {getSkillsForTree(selectedTree).map(skill => (
            <div key={skill.skillName} className="skill-item small-tag">
              <div className="skill-header">
                <h4>{skill.displayName}</h4>
                <div className="skill-level">
                  {renderSkillLevel(skill.currentLevel)}
                </div>
              </div>

              <p className="skill-description">{skill.description}</p>

              <div className="skill-status">
                {skill.currentLevel > 0 && (
                  <div className="current-effect">
                    Atual: <span className="bonus">{skill.currentBonus}</span>
                  </div>
                )}
                
                {skill.currentLevel < 3 && (
                  <div className="next-level">
                    Próximo nível: <span className="bonus">{skill.nextLevelBonus}</span>
                  </div>
                )}
              </div>

              {skill.currentLevel < 3 && (
                <button
                  className="invest-button authentic-anchor"
                  onClick={() => investSkillPoint(skill.skillName)}
                  disabled={!canInvest(skill)}
                >
                  {canInvest(skill) 
                    ? `Investir (${skill.costForNextLevel} pontos)`
                    : skill.currentLevel >= 3 
                      ? 'Máximo atingido'
                      : 'Pontos insuficientes'
                  }
                </button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CaptainProgression;