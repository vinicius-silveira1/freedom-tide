package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.CrewPersonality;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TavernRecruitDTO {
    // Informações que o jogador vê antes de recrutar
    private String name;
    private String background; // História pessoal do personagem
    private String catchphrase; // Frase característica
    private CrewPersonality personality;
    private int despairLevel; // Nível de Desespero (afeta salário e risco)
    private int salary; // Salário mensal exigido
    private int hiringCost; // Custo inicial de contratação
    private int initialMoral; // Moral inicial que o tripulante terá

    // Profissão e potencial
    private String profession; // Profissão baseada nos atributos
    private String professionDescription; // Descrição da profissão
    private String professionIcon; // Ícone visual da profissão (emoji)
    private String professionColor; // Cor CSS da profissão para destaque
    private int primaryAttribute; // Valor do atributo principal
    private String specialization; // Especialização (ex: "Especialista em Navegação")

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
