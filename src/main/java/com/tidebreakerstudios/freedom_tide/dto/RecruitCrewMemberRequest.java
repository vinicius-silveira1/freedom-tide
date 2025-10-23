package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.CrewPersonality;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para carregar os dados de uma requisição para recrutar um novo membro de tripulação.
 * Inclui validações para garantir a integridade dos dados de entrada.
 */
@Data
public class RecruitCrewMemberRequest {

    @NotBlank(message = "O nome do tripulante não pode ser vazio.")
    private String name;

    private String background; // História pessoal do personagem
    private String catchphrase; // Frase característica

    @NotNull(message = "A personalidade não pode ser nula.")
    private CrewPersonality personality;

    /**
     * Nível de Desespero no momento do recrutamento (1-10).
     */
    @NotNull
    @Min(value = 1, message = "O nível de desespero deve ser no mínimo 1.")
    @Max(value = 10, message = "O nível de desespero deve ser no máximo 10.")
    private Integer despairLevel;

    // --- Atributos (1-10) ---
    @NotNull
    @Min(1)
    @Max(10)
    private Integer navigation;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer artillery;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer combat;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer medicine;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer carpentry;

    @NotNull
    @Min(1) 
    @Max(10)
    private Integer intelligence;
}
