package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.CrewPersonality;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewProfession;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewRank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CrewMemberDetailDTO {
    // Informações básicas do tripulante
    private Long id;
    private String name;
    private String background;
    private String catchphrase;
    private CrewPersonality personality;
    private int salary;
    private int morale;

    // Atributos atuais
    private int navigation;
    private int artillery;
    private int combat;
    private int medicine;
    private int carpentry;
    private int intelligence;

    // Informações de progressão
    private CrewProfession profession;
    private String professionIcon;
    private String professionColor;
    private CrewRank currentRank;
    private CrewRank nextRank;
    private int currentXP;
    private int xpForCurrentRank;
    private int xpForNextRank;
    private double xpProgress; // Percentual de progresso (0.0 a 1.0)
    
    // Habilidades e benefícios
    private List<String> unlockedAbilities;
    private String rankDescription;
    private int totalCombats;
    private int totalRepairs;
    private int totalNavigations;
    private int totalContracts;
}