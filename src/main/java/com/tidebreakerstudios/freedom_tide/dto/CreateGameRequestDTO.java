package com.tidebreakerstudios.freedom_tide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para requisição de criação de um novo jogo.
 */
@Data
public class CreateGameRequestDTO {
    
    @NotBlank(message = "O nome do capitão não pode ser vazio.")
    @Size(min = 2, max = 50, message = "O nome do capitão deve ter entre 2 e 50 caracteres.")
    private String captainName;
}