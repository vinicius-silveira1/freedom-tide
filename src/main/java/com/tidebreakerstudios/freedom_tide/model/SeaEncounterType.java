package com.tidebreakerstudios.freedom_tide.model;

public enum SeaEncounterType {
    // Encontros básicos (sempre possíveis)
    MERCHANT_SHIP,      // Um navio mercante, possivelmente carregando mercadorias valiosas.
    PIRATE_VESSEL,      // Um navio pirata, provavelmente hostil.
    NAVY_PATROL,        // Uma patrulha da marinha imperial, procurando por problemas.
    MYSTERIOUS_WRECK,   // Os destroços de um navio, talvez com tesouros ou sobreviventes.
    
    // Encontros relacionados a contratos da GUILD
    GUILD_CONVOY,       // Comboio da Guilda transportando mercadorias valiosas
    TRADE_DISPUTE,      // Disputa comercial que precisa de mediação
    MERCHANT_DISTRESS,  // Mercador em apuros pedindo ajuda
    
    // Encontros relacionados a contratos do EMPIRE
    IMPERIAL_ESCORT,    // Escolta imperial transportando diplomatas ou tesouros
    REBEL_SABOTEURS,    // Sabotadores tentando interceptar recursos imperiais
    TAX_COLLECTORS,     // Coletores de impostos imperiais fazendo inspeções
    
    // Encontros relacionados a contratos da BROTHERHOOD
    SMUGGLER_MEET,      // Encontro secreto com contrabandistas
    IMPERIAL_PURSUIT,   // Perseguição imperial a atividades ilícitas
    PIRATE_ALLIANCE,    // Proposta de aliança com outros piratas
    
    // Encontros relacionados a contratos REVOLUTIONARY
    FREEDOM_FIGHTERS,   // Lutadores pela liberdade pedindo apoio
    IMPERIAL_OPPRESSION, // Testemunhar atos de opressão imperial
    UNDERGROUND_NETWORK  // Contato com a rede clandestina revolucionária
}
