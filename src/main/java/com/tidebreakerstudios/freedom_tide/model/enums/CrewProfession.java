package com.tidebreakerstudios.freedom_tide.model.enums;

import lombok.Getter;

/**
 * Define as profissões especializadas da tripulação.
 * Cada profissão tem requisitos específicos de atributos e oferece
 * habilidades únicas conforme o tripulante evolui.
 */
@Getter
public enum CrewProfession {
    
    // Profissões Primárias (baseadas em um atributo dominante)
    NAVIGATOR("Navegador", "navigation", 7, "Mestres dos ventos e correntes"),
    GUNNER("Artilheiro", "artillery", 7, "Senhores do ferro e da pólvora"), 
    FIGHTER("Combatente", "combat", 7, "Lobos do mar e guerreiros"),
    MEDIC("Médico", "medicine", 7, "Anjos da maré e curadores"),
    CARPENTER("Carpinteiro", "carpentry", 7, "Mestres da madeira e do ferro"),
    STRATEGIST("Estrategista", "intelligence", 7, "Cérebros da operação"),
    
    // Profissões Híbridas (baseadas em dois atributos)
    CORSAIR("Corsário", "combat,artillery", 6, "Especialistas em pirataria e saque"),
    EXPLORER("Explorador", "navigation,intelligence", 6, "Descobridores de segredos e tesouros"),
    BATTLE_MEDIC("Curandeiro de Batalha", "medicine,combat", 6, "Médicos de guerra e campo de batalha"),
    
    // Profissão Genérica
    SAILOR("Marinheiro", "", 0, "Tripulante comum, ainda sem especialização");

    private final String displayName;
    private final String requiredAttributes;
    private final int minimumAttributeValue;
    private final String description;

    CrewProfession(String displayName, String requiredAttributes, int minimumAttributeValue, String description) {
        this.displayName = displayName;
        this.requiredAttributes = requiredAttributes;
        this.minimumAttributeValue = minimumAttributeValue;
        this.description = description;
    }

    /**
     * Retorna o nome do arquivo do ícone de pixel art associado à profissão.
     */
    public String getIcon() {
        return switch (this) {
            case NAVIGATOR -> "navigator.png";
            case GUNNER -> "gunner.png";
            case FIGHTER -> "fighter.png";
            case MEDIC -> "medic.png";
            case CARPENTER -> "carpenter.png";
            case STRATEGIST -> "strategist.png";
            case CORSAIR -> "corsair.png";
            case EXPLORER -> "explorer.png";
            case BATTLE_MEDIC -> "battle_medic.png";
            case SAILOR -> "sailor.png";
        };
    }

    /**
     * Retorna a cor CSS associada à profissão para destaque visual.
     */
    public String getColor() {
        return switch (this) {
            case NAVIGATOR -> "#2E86AB";      // Azul oceânico
            case GUNNER -> "#A23B72";        // Roxo explosivo
            case FIGHTER -> "#F18F01";       // Laranja agressivo
            case MEDIC -> "#C73E1D";         // Vermelho médico
            case CARPENTER -> "#8B4513";     // Marrom madeira
            case STRATEGIST -> "#6A4C93";    // Roxo intelectual
            case CORSAIR -> "#2C1810";       // Marrom escuro pirata
            case EXPLORER -> "#228B22";      // Verde aventura
            case BATTLE_MEDIC -> "#DC143C";  // Vermelho escuro
            case SAILOR -> "#4682B4";        // Azul aço
        };
    }

    /**
     * Determina a profissão baseada nos atributos do tripulante.
     */
    public static CrewProfession determineProfession(int navigation, int artillery, int combat, 
                                                   int medicine, int carpentry, int intelligence) {
        
        // Verificar profissões híbridas primeiro (requisitos mais específicos)
        if (combat >= 6 && artillery >= 6) return CORSAIR;
        if (navigation >= 6 && intelligence >= 6) return EXPLORER;
        if (medicine >= 6 && combat >= 6) return BATTLE_MEDIC;
        
        // Verificar profissões primárias
        if (navigation >= 7) return NAVIGATOR;
        if (artillery >= 7) return GUNNER;
        if (combat >= 7) return FIGHTER;
        if (medicine >= 7) return MEDIC;
        if (carpentry >= 7) return CARPENTER;
        if (intelligence >= 7) return STRATEGIST;
        
        // Profissão padrão
        return SAILOR;
    }

    /**
     * Verifica se o tripulante pode evoluir para esta profissão.
     */
    public boolean canEvolveIntoProfession(int navigation, int artillery, int combat, 
                                         int medicine, int carpentry, int intelligence) {
        if (this.requiredAttributes.isEmpty()) return true; // SAILOR
        
        String[] attributes = this.requiredAttributes.split(",");
        for (String attr : attributes) {
            int value = switch (attr.trim()) {
                case "navigation" -> navigation;
                case "artillery" -> artillery;
                case "combat" -> combat;
                case "medicine" -> medicine;
                case "carpentry" -> carpentry;
                case "intelligence" -> intelligence;
                default -> 0;
            };
            if (value < this.minimumAttributeValue) return false;
        }
        return true;
    }
}