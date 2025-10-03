package com.tidebreakerstudios.freedom_tide.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para carregar os dados de uma requisição para resolver um evento narrativo.
 * Carrega o ID da opção que o jogador escolheu.
 */
@Data
public class ResolveEventRequest {

    @NotNull(message = "O ID da opção não pode ser nulo.")
    private Long optionId;

}
