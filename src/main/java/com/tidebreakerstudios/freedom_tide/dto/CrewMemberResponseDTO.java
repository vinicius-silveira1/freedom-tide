package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrewMemberResponseDTO {
    private Long id;
    private String name;
    private String background;
    private String catchphrase;
    private String personality;
    private Integer moral;
    private Integer loyalty;
    private Integer salary;
    private CrewAttributesDTO attributes;
    
    // Progressão
    private String profession;
    private String rank;
    private Integer experiencePoints;
    private Integer experienceToNextRank;
    private String progressSummary;
    
    // Estatísticas
    private Integer combatsParticipated;
    private Integer journeysCompleted;
    private Integer repairsPerformed;
    private Integer healingsPerformed;
    
    // Habilidades
    private String unlockedAbilities;
}
