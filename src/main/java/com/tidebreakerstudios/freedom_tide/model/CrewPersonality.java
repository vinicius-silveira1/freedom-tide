package com.tidebreakerstudios.freedom_tide.model;

/**
 * Define a ideologia de um membro da tripulação, determinando como
 * suas ações e as do capitão afetam seu moral.
 */
import lombok.Getter;

/**
 * Define a ideologia de um membro da tripulação, determinando como
 * suas ações e as do capitão afetam seu moral.
 */
@Getter
public enum CrewPersonality {
    /**
     * Prospera em ações que aumentam a Reputação. Despreza a pirataria.
     */
    HONEST(2),

    /**
     * Prospera em ações que aumentam a Infâmia. Gosta de combate e pilhagem.
     */
    BLOODTHIRSTY(0),

    /**
     * Prospera em ações que aumentam a Aliança. Apoia a luta contra a opressão.
     */
    REBEL(1),

    /**
     * Indiferente à ideologia, focado apenas no lucro pessoal.
     * Seu moral só é afetado pelo dinheiro.
     */
    GREEDY(-2);

    private final int moralModifier;

    CrewPersonality(int moralModifier) {
        this.moralModifier = moralModifier;
    }

}
