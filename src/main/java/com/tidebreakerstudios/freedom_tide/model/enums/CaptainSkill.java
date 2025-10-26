package com.tidebreakerstudios.freedom_tide.model.enums;

import lombok.Getter;

/**
 * Define as habilidades que o capitão pode desenvolver através de pontos de skill.
 * Organizadas em três árvores de especialização: Combat, Trade e Exploration.
 */
@Getter
public enum CaptainSkill {
    
    // === COMBAT TREE === 
    COMBAT_PROWESS("Prowess Militar", CaptainSkillTree.COMBAT, 
        "Aumenta o dano causado pelo seu navio em combate",
        new String[]{"Nível 1: +10% dano", "Nível 2: +20% dano", "Nível 3: +35% dano"}),
    
    NAVAL_TACTICS("Táticas Navais", CaptainSkillTree.COMBAT,
        "Aumenta a chance de acerto crítico em combate naval",
        new String[]{"Nível 1: +5% crítico", "Nível 2: +12% crítico", "Nível 3: +20% crítico"}),
    
    CREW_INSPIRATION("Inspiração da Tripulação", CaptainSkillTree.COMBAT,
        "Sua tripulação ganha mais experiência em combate",
        new String[]{"Nível 1: +25% XP", "Nível 2: +50% XP", "Nível 3: +100% XP"}),
    
    LEADERSHIP("Liderança", CaptainSkillTree.COMBAT,
        "Aumenta a capacidade máxima da sua tripulação",
        new String[]{"Nível 1: +2 tripulantes (máx. 5)", "Nível 2: +3 tripulantes (máx. 6)", "Nível 3: +5 tripulantes (máx. 8)"}),
    
    // === TRADE TREE ===
    MERCHANT_EYE("Olho do Comerciante", CaptainSkillTree.TRADE,
        "Aumenta os lucros obtidos com comércio",
        new String[]{"Nível 1: +15% lucro", "Nível 2: +30% lucro", "Nível 3: +50% lucro"}),
    
    NEGOTIATION("Negociação", CaptainSkillTree.TRADE,
        "Melhora os preços de compra e venda nos portos",
        new String[]{"Nível 1: +5% preços", "Nível 2: +12% preços", "Nível 3: +20% preços"}),
    
    ECONOMIC_MIND("Mente Econômica", CaptainSkillTree.TRADE,
        "Aumenta as recompensas em ouro dos contratos",
        new String[]{"Nível 1: +20% ouro", "Nível 2: +40% ouro", "Nível 3: +75% ouro"}),
    
    // === EXPLORATION TREE ===
    SEA_KNOWLEDGE("Conhecimento dos Mares", CaptainSkillTree.EXPLORATION,
        "Melhora a qualidade dos encontros marítimos",
        new String[]{"Nível 1: -10% encontros ruins", "Nível 2: -20% encontros ruins", "Nível 3: +15% encontros épicos"}),
    
    WEATHER_READING("Leitura do Tempo", CaptainSkillTree.EXPLORATION,
        "Reduz os danos causados por tempestades",
        new String[]{"Nível 1: -25% dano tempestade", "Nível 2: -50% dano tempestade", "Nível 3: Imune a tempestades"}),
    
    NAVIGATION_MASTER("Mestre da Navegação", CaptainSkillTree.EXPLORATION,
        "Acelera o tempo de viagem entre portos",
        new String[]{"Nível 1: -15% tempo viagem", "Nível 2: -30% tempo viagem", "Nível 3: -50% tempo viagem"});
    
    private final String displayName;
    private final CaptainSkillTree tree;
    private final String description;
    private final String[] levelDescriptions;
    
    CaptainSkill(String displayName, CaptainSkillTree tree, String description, String[] levelDescriptions) {
        this.displayName = displayName;
        this.tree = tree;
        this.description = description;
        this.levelDescriptions = levelDescriptions;
    }
    
    /**
     * Retorna a descrição específica para um nível da habilidade.
     */
    public String getLevelDescription(int level) {
        if (level < 1 || level > levelDescriptions.length) {
            return "Nível inválido";
        }
        return levelDescriptions[level - 1];
    }
    
    /**
     * Retorna o custo em pontos de skill para evoluir do nível atual para o próximo.
     */
    public int getCostForLevel(int targetLevel) {
        return switch (targetLevel) {
            case 1 -> 1;
            case 2 -> 2; 
            case 3 -> 3;
            default -> 0; // Nível máximo ou inválido
        };
    }
    
    /**
     * Calcula o valor do bônus baseado no nível da habilidade.
     */
    public double getBonusValue(int level) {
        return switch (this) {
            case COMBAT_PROWESS -> switch (level) {
                case 1 -> 0.10;
                case 2 -> 0.20;
                case 3 -> 0.35;
                default -> 0.0;
            };
            case NAVAL_TACTICS -> switch (level) {
                case 1 -> 0.05;
                case 2 -> 0.12;
                case 3 -> 0.20;
                default -> 0.0;
            };
            case CREW_INSPIRATION -> switch (level) {
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 1.00;
                default -> 0.0;
            };
            case MERCHANT_EYE -> switch (level) {
                case 1 -> 0.15;
                case 2 -> 0.30;
                case 3 -> 0.50;
                default -> 0.0;
            };
            case NEGOTIATION -> switch (level) {
                case 1 -> 0.05;
                case 2 -> 0.12;
                case 3 -> 0.20;
                default -> 0.0;
            };
            case ECONOMIC_MIND -> switch (level) {
                case 1 -> 0.20;
                case 2 -> 0.40;
                case 3 -> 0.75;
                default -> 0.0;
            };
            case SEA_KNOWLEDGE -> switch (level) {
                case 1 -> 0.10; // redução de encontros ruins
                case 2 -> 0.20;
                case 3 -> 0.15; // aumento de encontros épicos (diferente)
                default -> 0.0;
            };
            case WEATHER_READING -> switch (level) {
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 1.00; // imunidade total
                default -> 0.0;
            };
            case NAVIGATION_MASTER -> switch (level) {
                case 1 -> 0.15;
                case 2 -> 0.30;
                case 3 -> 0.50;
                default -> 0.0;
            };
            case LEADERSHIP -> switch (level) {
                case 1 -> 2.0; // +2 tripulantes (máximo 5)
                case 2 -> 3.0; // +3 tripulantes (máximo 6)
                case 3 -> 5.0; // +5 tripulantes (máximo 8)
                default -> 0.0;
            };
        };
    }
}