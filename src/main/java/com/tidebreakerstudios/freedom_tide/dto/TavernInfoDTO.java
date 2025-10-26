package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TavernInfoDTO {
    // Informações da capacidade da tripulação
    private int currentCrewSize;
    private int maxCrewCapacity;
    private boolean canRecruitMore;
    
    // Lista de recrutas disponíveis
    private List<TavernRecruitDTO> availableRecruits;
    
    // Informações adicionais da taverna
    private String tavernMessage;
}