package com.tidebreakerstudios.freedom_tide.model.enums;

import lombok.Getter;

/**
 * Define as três árvores de especialização do capitão.
 * Cada árvore oferece um caminho de desenvolvimento diferente.
 */
@Getter
public enum CaptainSkillTree {
    
    COMBAT("🗡️ Combate", "Especialização em batalhas navais e liderança militar",
        "Focado em maximizar eficácia em combate e experiência da tripulação"),
    
    TRADE("💰 Comércio", "Especialização em economia e negociação",
        "Focado em maximizar lucros e recompensas financeiras"),
    
    EXPLORATION("🧭 Exploração", "Especialização em navegação e descobertas",
        "Focado em melhorar viagens e encontros marítimos");
    
    private final String displayName;
    private final String description;
    private final String summary;
    
    CaptainSkillTree(String displayName, String description, String summary) {
        this.displayName = displayName;
        this.description = description;
        this.summary = summary;
    }
}