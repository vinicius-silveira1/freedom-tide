package com.tidebreakerstudios.freedom_tide.model;

/**
 * Define os tipos de portos existentes no mundo,
 * cada um com suas afiliações e características únicas.
 */
public enum PortType {
    /**
     * Um porto controlado pelo Império de Alvor. Focado em lei e ordem (para os ricos).
     */
    IMPERIAL,

    /**
     * Um porto dominado pela Guilda Mercante Unida. O centro do comércio e do capital.
     */
    GUILD,

    /**
     * Um refúgio para piratas e foras da lei, associado à Irmandade de Grani.
     */
    PIRATE,

    /**
     * Um porto independente que faz parte da Federação de Portos Livres (se o jogador a criar).
     */
    FREE
}
