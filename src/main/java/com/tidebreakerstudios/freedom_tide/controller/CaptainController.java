package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.dto.captain.CaptainProgressionDTO;
import com.tidebreakerstudios.freedom_tide.dto.captain.CaptainSkillDTO;
import com.tidebreakerstudios.freedom_tide.dto.captain.CaptainSkillInvestmentDTO;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.enums.CaptainSkill;
import com.tidebreakerstudios.freedom_tide.service.CaptainProgressionService;
import com.tidebreakerstudios.freedom_tide.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelas operações relacionadas à progressão do capitão.
 */
@RestController
@RequestMapping("/api/games/{gameId}/captain")
@RequiredArgsConstructor
public class CaptainController {

    private final GameService gameService;
    private final CaptainProgressionService captainProgressionService;

    /**
     * Obtém o estado atual de progressão do capitão.
     */
    @GetMapping("/progression")
    public ResponseEntity<CaptainProgressionDTO> getCaptainProgression(@PathVariable Long gameId) {
        try {
            Game game = gameService.findGameById(gameId);
            CaptainProgressionDTO progression = buildCaptainProgressionDTO(game);
            return ResponseEntity.ok(progression);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Investe um ponto de skill em uma habilidade específica.
     */
    @PostMapping("/invest-skill")
    public ResponseEntity<CaptainProgressionDTO> investSkillPoint(
            @PathVariable Long gameId,
            @RequestBody CaptainSkillInvestmentDTO investment) {
        try {
            CaptainSkill skill = CaptainSkill.valueOf(investment.getSkillName().toUpperCase());
            boolean success = captainProgressionService.investSkillPoint(gameId, skill);
            
            if (success) {
                Game game = gameService.findGameById(gameId);
                CaptainProgressionDTO progression = buildCaptainProgressionDTO(game);
                return ResponseEntity.ok(progression);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Constrói o DTO de progressão do capitão baseado no estado atual do jogo.
     */
    private CaptainProgressionDTO buildCaptainProgressionDTO(Game game) {
        return CaptainProgressionDTO.builder()
                .captainName(game.getCaptainName())
                .currentLevel(game.getCaptainLevel())
                .currentXP(game.getCaptainXP())
                .xpForNextLevel(captainProgressionService.getXPForNextLevel(game))
                .availableSkillPoints(game.getCaptainSkillPoints())
                
                // Árvore de combate
                .combatProwess(buildSkillDTO(game, CaptainSkill.COMBAT_PROWESS))
                .navalTactics(buildSkillDTO(game, CaptainSkill.NAVAL_TACTICS))
                .crewInspiration(buildSkillDTO(game, CaptainSkill.CREW_INSPIRATION))
                
                // Árvore de comércio
                .merchantEye(buildSkillDTO(game, CaptainSkill.MERCHANT_EYE))
                .negotiation(buildSkillDTO(game, CaptainSkill.NEGOTIATION))
                .economicMind(buildSkillDTO(game, CaptainSkill.ECONOMIC_MIND))
                
                // Árvore de exploração
                .seaKnowledge(buildSkillDTO(game, CaptainSkill.SEA_KNOWLEDGE))
                .weatherReading(buildSkillDTO(game, CaptainSkill.WEATHER_READING))
                .navigationMaster(buildSkillDTO(game, CaptainSkill.NAVIGATION_MASTER))
                
                .build();
    }

    /**
     * Constrói o DTO de uma habilidade específica.
     */
    private CaptainSkillDTO buildSkillDTO(Game game, CaptainSkill skill) {
        int currentLevel = captainProgressionService.getSkillLevel(game, skill);
        boolean canLevelUp = captainProgressionService.canInvestInSkill(game, skill);
        int costForNextLevel = currentLevel < 3 ? skill.getCostForLevel(currentLevel + 1) : 0;
        
        return CaptainSkillDTO.builder()
                .skillName(skill.name())
                .displayName(skill.getDisplayName())
                .description(skill.getDescription())
                .treeType(skill.getTree().name())
                .currentLevel(currentLevel)
                .maxLevel(3)
                .costForNextLevel(costForNextLevel)
                .canLevelUp(canLevelUp)
                .currentLevelDescription(currentLevel > 0 ? skill.getLevelDescription(currentLevel) : "Não aprendida")
                .nextLevelDescription(currentLevel < 3 ? skill.getLevelDescription(currentLevel + 1) : "Nível máximo")
                .activeBonus(captainProgressionService.getActiveBonus(game, skill))
                .build();
    }
}