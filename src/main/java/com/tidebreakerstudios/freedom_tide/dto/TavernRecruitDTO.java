package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.CrewPersonality;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TavernRecruitDTO {
    // Informações que o jogador vê antes de recrutar
    private String name;
    private CrewPersonality personality;
    private int despairLevel; // Nível de Desespero (afeta salário e risco)
    private int salary; // Salário exigido
    private int initialMoral; // Moral inicial que o tripulante terá

    // Atributos do tripulante
    private int navigation;
    private int artillery;
    private int combat;
    private int medicine;
    private int carpentry;
    private int intelligence;

    // O DTO de requisição para efetivar o recrutamento, pré-populado
    private RecruitCrewMemberRequest recruitRequest;
}
