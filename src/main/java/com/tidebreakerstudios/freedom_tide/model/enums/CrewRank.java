package com.tidebreakerstudios.freedom_tide.model.enums;

import lombok.Getter;

/**
 * Define os ranks (níveis) que um tripulante pode alcançar em sua profissão.
 * Cada rank oferece benefícios específicos e representa o crescimento do personagem.
 */
@Getter
public enum CrewRank {
    
    // Ranks Universais (todos começam aqui)
    RECRUIT("Recruta", 0, 0, "Novo membro da tripulação"),
    
    // Navegador
    NAVIGATOR_APPRENTICE("Grumete", 1, 100, "Aprendiz de navegação"),
    NAVIGATOR_SAILOR("Marinheiro", 2, 300, "Navegador competente"),
    NAVIGATOR_QUARTERMASTER("Contramestre", 3, 600, "Oficial de navegação"),
    NAVIGATOR_CAPTAIN("Capitão de Navegação", 4, 1000, "Mestre dos ventos"),
    NAVIGATOR_LEGEND("Lenda dos Mares", 5, 1500, "Navegador lendário"),
    
    // Artilheiro  
    GUNNER_POWDER("Pólvora", 1, 100, "Auxiliar de artilharia"),
    GUNNER_CANNON("Canhoneiro", 2, 300, "Operador de canhões"),
    GUNNER_MASTER("Mestre Artilheiro", 3, 600, "Especialista em bombardeio"),
    GUNNER_DEMON("Demônio dos Canhões", 4, 1000, "Terror dos mares"),
    GUNNER_LEGEND("Destruidor Lendário", 5, 1500, "Artilheiro lendário"),
    
    // Combatente
    FIGHTER_RECRUIT("Soldado", 1, 100, "Guerreiro básico"),
    FIGHTER_VETERAN("Veterano", 2, 300, "Lutador experiente"),
    FIGHTER_ELITE("Elite", 3, 600, "Combatente de elite"),
    FIGHTER_LEGEND("Lenda Viva", 4, 1000, "Guerreiro lendário"),
    FIGHTER_MYTH("Mito dos Combates", 5, 1500, "Combatente mítico"),
    
    // Médico
    MEDIC_NURSE("Enfermeiro", 1, 100, "Assistente médico"),
    MEDIC_SURGEON("Cirurgião", 2, 300, "Médico competente"),
    MEDIC_DOCTOR("Médico Naval", 3, 600, "Especialista em medicina naval"),
    MEDIC_MIRACLE("Salvador Milagroso", 4, 1000, "Curandeiro excepcional"),
    MEDIC_LEGEND("Anjo da Maré", 5, 1500, "Médico lendário"),
    
    // Carpinteiro
    CARPENTER_APPRENTICE("Aprendiz", 1, 100, "Auxiliar de carpintaria"),
    CARPENTER_CRAFTSMAN("Oficial", 2, 300, "Carpinteiro competente"),
    CARPENTER_MASTER("Mestre Construtor", 3, 600, "Especialista em construção naval"),
    CARPENTER_LEGEND("Lenda dos Estaleiros", 4, 1000, "Construtor lendário"),
    CARPENTER_MYTH("Mestre Mítico", 5, 1500, "Carpinteiro mítico"),
    
    // Estrategista
    STRATEGIST_SCRIBE("Escriba", 1, 100, "Assistente intelectual"),
    STRATEGIST_ADVISOR("Conselheiro", 2, 300, "Conselheiro tático"),
    STRATEGIST_MASTER("Estrategista", 3, 600, "Mestre em táticas"),
    STRATEGIST_GENIUS("Gênio Tático", 4, 1000, "Estrategista genial"),
    STRATEGIST_LEGEND("Mente Lendária", 5, 1500, "Estrategista lendário"),
    
    // Ranks Híbridos
    CORSAIR_PRIVATEER("Corsário", 1, 150, "Pirata licenciado"),
    CORSAIR_CAPTAIN("Capitão Corsário", 2, 400, "Comandante pirata"),
    CORSAIR_TERROR("Terror dos Mares", 3, 700, "Corsário temido"),
    CORSAIR_LEGEND("Lenda Pirata", 4, 1100, "Corsário lendário"),
    
    EXPLORER_SCOUT("Batedor", 1, 150, "Explorador iniciante"),
    EXPLORER_PATHFINDER("Desbravador", 2, 400, "Descobridor de rotas"),
    EXPLORER_MASTER("Mestre Explorador", 3, 700, "Explorador experiente"),
    EXPLORER_LEGEND("Lenda da Exploração", 4, 1100, "Explorador lendário"),
    
    BATTLE_MEDIC_FIELD("Médico de Campo", 1, 150, "Curandeiro de batalha"),
    BATTLE_MEDIC_COMBAT("Médico de Combate", 2, 400, "Especialista em medicina de guerra"),
    BATTLE_MEDIC_LEGEND("Anjo da Guerra", 3, 700, "Curandeiro de batalha lendário");

    private final String displayName;
    private final int level;
    private final int requiredXP;
    private final String description;

    CrewRank(String displayName, int level, int requiredXP, String description) {
        this.displayName = displayName;
        this.level = level;
        this.requiredXP = requiredXP;
        this.description = description;
    }

    /**
     * Retorna o próximo rank na progressão da profissão.
     */
    public CrewRank getNextRank(CrewProfession profession) {
        return switch (profession) {
            case NAVIGATOR -> switch (this) {
                case RECRUIT -> NAVIGATOR_APPRENTICE;
                case NAVIGATOR_APPRENTICE -> NAVIGATOR_SAILOR;
                case NAVIGATOR_SAILOR -> NAVIGATOR_QUARTERMASTER;
                case NAVIGATOR_QUARTERMASTER -> NAVIGATOR_CAPTAIN;
                case NAVIGATOR_CAPTAIN -> NAVIGATOR_LEGEND;
                default -> this;
            };
            case GUNNER -> switch (this) {
                case RECRUIT -> GUNNER_POWDER;
                case GUNNER_POWDER -> GUNNER_CANNON;
                case GUNNER_CANNON -> GUNNER_MASTER;
                case GUNNER_MASTER -> GUNNER_DEMON;
                case GUNNER_DEMON -> GUNNER_LEGEND;
                default -> this;
            };
            case FIGHTER -> switch (this) {
                case RECRUIT -> FIGHTER_RECRUIT;
                case FIGHTER_RECRUIT -> FIGHTER_VETERAN;
                case FIGHTER_VETERAN -> FIGHTER_ELITE;
                case FIGHTER_ELITE -> FIGHTER_LEGEND;
                case FIGHTER_LEGEND -> FIGHTER_MYTH;
                default -> this;
            };
            case MEDIC -> switch (this) {
                case RECRUIT -> MEDIC_NURSE;
                case MEDIC_NURSE -> MEDIC_SURGEON;
                case MEDIC_SURGEON -> MEDIC_DOCTOR;
                case MEDIC_DOCTOR -> MEDIC_MIRACLE;
                case MEDIC_MIRACLE -> MEDIC_LEGEND;
                default -> this;
            };
            case CARPENTER -> switch (this) {
                case RECRUIT -> CARPENTER_APPRENTICE;
                case CARPENTER_APPRENTICE -> CARPENTER_CRAFTSMAN;
                case CARPENTER_CRAFTSMAN -> CARPENTER_MASTER;
                case CARPENTER_MASTER -> CARPENTER_LEGEND;
                case CARPENTER_LEGEND -> CARPENTER_MYTH;
                default -> this;
            };
            case STRATEGIST -> switch (this) {
                case RECRUIT -> STRATEGIST_SCRIBE;
                case STRATEGIST_SCRIBE -> STRATEGIST_ADVISOR;
                case STRATEGIST_ADVISOR -> STRATEGIST_MASTER;
                case STRATEGIST_MASTER -> STRATEGIST_GENIUS;
                case STRATEGIST_GENIUS -> STRATEGIST_LEGEND;
                default -> this;
            };
            case CORSAIR -> switch (this) {
                case RECRUIT -> CORSAIR_PRIVATEER;
                case CORSAIR_PRIVATEER -> CORSAIR_CAPTAIN;
                case CORSAIR_CAPTAIN -> CORSAIR_TERROR;
                case CORSAIR_TERROR -> CORSAIR_LEGEND;
                default -> this;
            };
            case EXPLORER -> switch (this) {
                case RECRUIT -> EXPLORER_SCOUT;
                case EXPLORER_SCOUT -> EXPLORER_PATHFINDER;
                case EXPLORER_PATHFINDER -> EXPLORER_MASTER;
                case EXPLORER_MASTER -> EXPLORER_LEGEND;
                default -> this;
            };
            case BATTLE_MEDIC -> switch (this) {
                case RECRUIT -> BATTLE_MEDIC_FIELD;
                case BATTLE_MEDIC_FIELD -> BATTLE_MEDIC_COMBAT;
                case BATTLE_MEDIC_COMBAT -> BATTLE_MEDIC_LEGEND;
                default -> this;
            };
            default -> this; // SAILOR permanece como RECRUIT
        };
    }
}