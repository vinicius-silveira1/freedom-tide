package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CrewManagementDTO {
    // Estatísticas gerais da tripulação
    private int totalCrewMembers;
    private double averageMorale;
    private int totalSalaryExpenses;
    
    // Estatísticas de XP
    private int totalXPEarned;
    private int totalCombatsParticipated;
    private int totalRepairsCompleted;
    private int totalNavigationsCompleted;
    private int totalContractsCompleted;
    
    // Lista detalhada de cada tripulante
    private List<CrewMemberDetailDTO> crewMembers;
    
    // Resumo por profissão
    private List<ProfessionSummaryDTO> professionSummaries;
}

