package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.enums.CaptainSkill;
import com.tidebreakerstudios.freedom_tide.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por gerenciar a progressão e evolução do capitão.
 * Implementa as mecânicas de XP, níveis e sistema de habilidades.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptainProgressionService {

    private final GameRepository gameRepository;

    // Constantes de progressão
    private static final int BASE_XP_PER_LEVEL = 100;
    private static final double XP_SCALING_FACTOR = 1.5;
    private static final int SKILL_POINTS_PER_LEVEL = 1;
    private static final int MAX_CAPTAIN_LEVEL = 20;
    private static final int MAX_SKILL_LEVEL = 3;

    /**
     * Concede XP ao capitão e verifica se há level up.
     */
    @Transactional
    public List<String> awardCaptainXP(Game game, int xpGained, String source) {
        List<String> progressMessages = new ArrayList<>();
        
        if (game.getCaptainLevel() >= MAX_CAPTAIN_LEVEL) {
            return progressMessages; // Capitão já no nível máximo
        }
        
        int currentXP = game.getCaptainXP();
        int newXP = currentXP + xpGained;
        game.setCaptainXP(newXP);
        
        progressMessages.add(String.format("⭐ Capitão %s ganhou %d XP (%s)", 
            game.getCaptainName(), xpGained, source));
        
        // Verificar level ups
        boolean leveledUp = false;
        while (game.getCaptainLevel() < MAX_CAPTAIN_LEVEL && 
               game.getCaptainXP() >= getRequiredXPForLevel(game.getCaptainLevel() + 1)) {
            leveledUp = true;
            levelUpCaptain(game, progressMessages);
        }
        
        if (leveledUp) {
            gameRepository.save(game);
        }
        
        return progressMessages;
    }
    
    /**
     * Realiza o level up do capitão.
     */
    private void levelUpCaptain(Game game, List<String> progressMessages) {
        int newLevel = game.getCaptainLevel() + 1;
        game.setCaptainLevel(newLevel);
        
        // Conceder pontos de skill
        int skillPointsGained = SKILL_POINTS_PER_LEVEL;
        game.setCaptainSkillPoints(game.getCaptainSkillPoints() + skillPointsGained);
        
        progressMessages.add(String.format("🎉 NÍVEL SUBIU! Capitão %s agora é nível %d!", 
            game.getCaptainName(), newLevel));
        progressMessages.add(String.format("✨ Você ganhou %d ponto(s) de habilidade!", skillPointsGained));
        
        log.info("Captain {} leveled up to level {}", game.getCaptainName(), newLevel);
    }
    
    /**
     * Calcula o XP necessário para alcançar um nível específico.
     */
    public int getRequiredXPForLevel(int level) {
        if (level <= 1) return 0;
        
        int totalXP = 0;
        for (int i = 2; i <= level; i++) {
            totalXP += (int) Math.ceil(BASE_XP_PER_LEVEL * Math.pow(XP_SCALING_FACTOR, i - 2));
        }
        return totalXP;
    }
    
    /**
     * Calcula o XP necessário para o próximo nível.
     */
    public int getXPForNextLevel(Game game) {
        if (game.getCaptainLevel() >= MAX_CAPTAIN_LEVEL) {
            return 0; // Já no nível máximo
        }
        
        int currentXP = game.getCaptainXP();
        int requiredXP = getRequiredXPForLevel(game.getCaptainLevel() + 1);
        return requiredXP - currentXP;
    }
    
    /**
     * Investe pontos de skill em uma habilidade específica.
     */
    @Transactional
    public boolean investSkillPoint(Long gameId, CaptainSkill skill) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
        
        // Verificar se há pontos disponíveis
        if (game.getCaptainSkillPoints() < 1) {
            return false;
        }
        
        // Obter nível atual da habilidade
        int currentLevel = getSkillLevel(game, skill);
        
        // Verificar se pode evoluir
        if (currentLevel >= MAX_SKILL_LEVEL) {
            return false;
        }
        
        // Verificar se tem pontos suficientes para o próximo nível
        int requiredPoints = skill.getCostForLevel(currentLevel + 1);
        if (game.getCaptainSkillPoints() < requiredPoints) {
            return false;
        }
        
        // Aplicar o investimento
        setSkillLevel(game, skill, currentLevel + 1);
        game.setCaptainSkillPoints(game.getCaptainSkillPoints() - requiredPoints);
        
        gameRepository.save(game);
        
        log.info("Captain {} invested {} skill points in {} (now level {})", 
            game.getCaptainName(), requiredPoints, skill.getDisplayName(), currentLevel + 1);
        
        return true;
    }
    
    /**
     * Obtém o nível atual de uma habilidade específica.
     */
    public int getSkillLevel(Game game, CaptainSkill skill) {
        return switch (skill) {
            case COMBAT_PROWESS -> game.getSkillCombatProwess();
            case NAVAL_TACTICS -> game.getSkillNavalTactics();
            case CREW_INSPIRATION -> game.getSkillCrewInspiration();
            case LEADERSHIP -> game.getSkillLeadership();
            case MERCHANT_EYE -> game.getSkillMerchantEye();
            case NEGOTIATION -> game.getSkillNegotiation();
            case ECONOMIC_MIND -> game.getSkillEconomicMind();
            case SEA_KNOWLEDGE -> game.getSkillSeaKnowledge();
            case WEATHER_READING -> game.getSkillWeatherReading();
            case NAVIGATION_MASTER -> game.getSkillNavigationMaster();
        };
    }
    
    /**
     * Define o nível de uma habilidade específica.
     */
    private void setSkillLevel(Game game, CaptainSkill skill, int level) {
        switch (skill) {
            case COMBAT_PROWESS -> game.setSkillCombatProwess(level);
            case NAVAL_TACTICS -> game.setSkillNavalTactics(level);
            case CREW_INSPIRATION -> game.setSkillCrewInspiration(level);
            case LEADERSHIP -> game.setSkillLeadership(level);
            case MERCHANT_EYE -> game.setSkillMerchantEye(level);
            case NEGOTIATION -> game.setSkillNegotiation(level);
            case ECONOMIC_MIND -> game.setSkillEconomicMind(level);
            case SEA_KNOWLEDGE -> game.setSkillSeaKnowledge(level);
            case WEATHER_READING -> game.setSkillWeatherReading(level);
            case NAVIGATION_MASTER -> game.setSkillNavigationMaster(level);
        }
    }
    
    /**
     * Calcula o valor do bônus ativo para uma habilidade específica.
     */
    public double getActiveBonus(Game game, CaptainSkill skill) {
        int level = getSkillLevel(game, skill);
        return skill.getBonusValue(level);
    }
    
    /**
     * Verifica se o capitão pode investir em uma habilidade específica.
     */
    public boolean canInvestInSkill(Game game, CaptainSkill skill) {
        int currentLevel = getSkillLevel(game, skill);
        if (currentLevel >= MAX_SKILL_LEVEL) {
            return false;
        }
        
        int requiredPoints = skill.getCostForLevel(currentLevel + 1);
        return game.getCaptainSkillPoints() >= requiredPoints;
    }

    // ==================== MÉTODOS DE BÔNUS ====================

    /**
     * Calcula o bônus de artilharia do capitão.
     */
    public double getArtilleryBonus(Game game) {
        int combatLevel = getSkillLevel(game, CaptainSkill.COMBAT_PROWESS);
        return 1.0 + (combatLevel * 0.15); // +15% por nível
    }

    /**
     * Calcula o bônus geral de combate do capitão.
     */
    public double getCombatBonus(Game game) {
        int tacticsLevel = getSkillLevel(game, CaptainSkill.NAVAL_TACTICS);
        return 1.0 + (tacticsLevel * 0.10); // +10% por nível
    }

    /**
     * Calcula o bônus de liderança (para ouro e moral) do capitão.
     */
    public double getLeadershipBonus(Game game) {
        int leadershipLevel = getSkillLevel(game, CaptainSkill.CREW_INSPIRATION);
        int economicLevel = getSkillLevel(game, CaptainSkill.ECONOMIC_MIND);
        double crewBonus = 1.0 + (leadershipLevel * 0.15); // +15% por nível
        double goldBonus = 1.0 + (economicLevel * 0.25);   // +25% por nível
        return crewBonus * goldBonus; // Bônus multiplicativos
    }

    /**
     * Calcula o bônus de negociação (compra/venda) do capitão.
     */
    public double getTradingBonus(Game game) {
        int negotiationLevel = getSkillLevel(game, CaptainSkill.NEGOTIATION);
        int merchantLevel = getSkillLevel(game, CaptainSkill.MERCHANT_EYE);
        double negotiationBonus = 1.0 + (negotiationLevel * 0.12); // +12% por nível
        double merchantBonus = 1.0 + (merchantLevel * 0.18);       // +18% por nível
        return negotiationBonus * merchantBonus; // Bônus multiplicativos
    }

    /**
     * Calcula o bônus de navegação (redução de consumo) do capitão.
     */
    public double getNavigationBonus(Game game) {
        int navigationLevel = getSkillLevel(game, CaptainSkill.NAVIGATION_MASTER);
        int weatherLevel = getSkillLevel(game, CaptainSkill.WEATHER_READING);
        double navigationBonus = 1.0 + (navigationLevel * 0.20); // +20% eficiência por nível
        double weatherBonus = 1.0 + (weatherLevel * 0.15);      // +15% eficiência por nível
        return navigationBonus * weatherBonus; // Bônus multiplicativos
    }

    /**
     * Calcula o bônus de economia (gestão de recursos) do capitão.
     */
    public double getEconomyBonus(Game game) {
        int merchantLevel = getSkillLevel(game, CaptainSkill.MERCHANT_EYE);
        return 1.0 + (merchantLevel * 0.15); // +15% por nível
    }

    /**
     * Calcula o bônus de descoberta (para encontros especiais) do capitão.
     */
    public double getDiscoveryBonus(Game game) {
        int seaKnowledgeLevel = getSkillLevel(game, CaptainSkill.SEA_KNOWLEDGE);
        return 1.0 + (seaKnowledgeLevel * 0.25); // +25% chance por nível
    }

    /**
     * Calcula a capacidade máxima da tripulação baseada no nível do capitão e skill de liderança.
     */
    public int getMaxCrewCapacity(Game game) {
        int baseCapacity = 3; // Capacidade base
        int leadershipLevel = getSkillLevel(game, CaptainSkill.LEADERSHIP);
        
        return switch (leadershipLevel) {
            case 1 -> baseCapacity + 2; // 5 tripulantes
            case 2 -> baseCapacity + 3; // 6 tripulantes  
            case 3 -> baseCapacity + 5; // 8 tripulantes
            default -> baseCapacity;    // 3 tripulantes (sem skill)
        };
    }
}