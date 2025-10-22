package com.tidebreakerstudios.freedom_tide.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para a requisição de escolha na sequência de introdução.
 */
@Data
public class IntroChoiceRequestDTO {
    
    @NotBlank(message = "A escolha é obrigatória")
    private String choice;
}