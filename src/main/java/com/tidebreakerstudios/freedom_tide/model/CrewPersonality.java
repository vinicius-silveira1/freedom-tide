package com.tidebreakerstudios.freedom_tide.model;

/**
 * Define a ideologia de um membro da tripulação, determinando como
 * suas ações e as do capitão afetam seu moral.
 */
public enum CrewPersonality {
    /**
     * Prospera em ações que aumentam a Reputação. Despreza a pirataria.
     */
    HONEST,

    /**
     * Prospera em ações que aumentam a Infâmia. Gosta de combate e pilhagem.
     */
    BLOODTHIRSTY,

    /**
     * Prospera em ações que aumentam a Aliança. Apoia a luta contra a opressão.
     */
    REBEL,

    /**
     * Indiferente à ideologia, focado apenas no lucro pessoal.
     * Seu moral só é afetado pelo dinheiro.
     */
    GREEDY
}
