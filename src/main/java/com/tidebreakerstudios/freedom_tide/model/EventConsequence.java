package com.tidebreakerstudios.freedom_tide.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Uma classe @Embeddable que agrupa todos os possíveis resultados de uma escolha de evento.
 * Não é uma tabela separada no banco, seus campos são "embutidos" na tabela da entidade que a usa (EventOption).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EventConsequence {

    // Consequências na Bússola do Capitão
    @Builder.Default
    private Integer reputationChange = 0;
    @Builder.Default
    private Integer infamyChange = 0;
    @Builder.Default
    private Integer allianceChange = 0;

    // Consequências nos Recursos do Navio
    @Builder.Default
    private Integer goldChange = 0;
    @Builder.Default
    private Integer foodChange = 0;
    @Builder.Default
    private Integer rumChange = 0;

    /**
     * A mudança de moral aplicada à tripulação. Pode ser positiva ou negativa.
     * A lógica de serviço poderá aplicar isso de forma diferente com base na personalidade do tripulante.
     */
    @Builder.Default
    private Integer crewMoralChange = 0;
}
