package com.tidebreakerstudios.freedom_tide.model;

import lombok.Getter;

/**
 * Enum que define as classes de navios disponíveis no jogo,
 * cada uma com um foco estratégico diferente.
 */
@Getter
public enum ShipType {
    /**
     * Ágil e rápido, ideal para exploração e fugas.
     */
    SCHOONER(100),

    /**
     * Versátil, um equilíbrio entre carga, velocidade e poder de fogo.
     */
    BRIG(150),

    /**
     * Poderoso navio de guerra, lento mas devastador em combate.
     */
    FRIGATE(250),

    /**
     * Agressivo e focado em abordagem, a escolha da Irmandade de Grani.
     */
    DRAKAR(120);

    private final int maxHull;

    ShipType(int maxHull) {
        this.maxHull = maxHull;
    }

}
