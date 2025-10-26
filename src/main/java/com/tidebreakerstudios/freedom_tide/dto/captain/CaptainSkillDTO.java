package com.tidebreakerstudios.freedom_tide.dto.captain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa uma habilidade específica do capitão.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptainSkillDTO {
    
    private String skillName;
    private String displayName;
    private String description;
    private String treeType;
    private Integer currentLevel;
    private Integer maxLevel;
    private Integer costForNextLevel;
    private Boolean canLevelUp;
    private String currentLevelDescription;
    private String nextLevelDescription;
    private Double activeBonus;
}