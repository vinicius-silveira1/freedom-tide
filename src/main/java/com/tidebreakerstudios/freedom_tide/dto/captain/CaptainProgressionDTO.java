package com.tidebreakerstudios.freedom_tide.dto.captain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa o estado de progressão do capitão.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptainProgressionDTO {
    
    private String captainName;
    private Integer currentLevel;
    private Integer currentXP;
    private Integer xpForNextLevel;
    private Integer availableSkillPoints;
    
    // Habilidades da árvore de combate
    private CaptainSkillDTO combatProwess;
    private CaptainSkillDTO navalTactics;
    private CaptainSkillDTO crewInspiration;
    
    // Habilidades da árvore de comércio
    private CaptainSkillDTO merchantEye;
    private CaptainSkillDTO negotiation;
    private CaptainSkillDTO economicMind;
    
    // Habilidades da árvore de exploração
    private CaptainSkillDTO seaKnowledge;
    private CaptainSkillDTO weatherReading;
    private CaptainSkillDTO navigationMaster;
}