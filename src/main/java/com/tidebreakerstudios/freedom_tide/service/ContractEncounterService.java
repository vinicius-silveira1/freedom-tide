package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Serviço responsável por gerar encontros marítimos baseados em contratos ativos.
 * Este serviço implementa a lógica de prioridade total para contratos, garantindo que
 * jogadores com contratos ativos tenham SEMPRE encontros relacionados à sua missão.
 */
@Service
@RequiredArgsConstructor
public class ContractEncounterService {

    private static final Random random = new Random();
    
    // Peso para encontros relacionados ao contrato (100% de chance)
    private static final double CONTRACT_ENCOUNTER_WEIGHT = 1.0;

    /**
     * Gera um tipo de encontro baseado no contrato ativo do jogo.
     * Se há contrato ativo, SEMPRE gera encontro relacionado ao contrato (100%).
     * Se não há contrato ativo, usa a lógica normal de encontros aleatórios.
     * 
     * @param game Estado atual do jogo
     * @return Tipo de encontro apropriado
     */
    public SeaEncounterType generateEncounterType(Game game) {
        Contract activeContract = game.getActiveContract();
        
        // Se não há contrato ativo, usa encontros básicos
        if (activeContract == null) {
            return generateBasicEncounter();
        }
        
        // 100% de chance de encontro relacionado ao contrato
        return generateContractRelatedEncounter(activeContract);
    }
    
    /**
     * Gera encontros básicos (sistema original)
     */
    private SeaEncounterType generateBasicEncounter() {
        SeaEncounterType[] basicTypes = {
            SeaEncounterType.MERCHANT_SHIP,
            SeaEncounterType.PIRATE_VESSEL,
            SeaEncounterType.NAVY_PATROL,
            SeaEncounterType.MYSTERIOUS_WRECK
        };
        return basicTypes[random.nextInt(basicTypes.length)];
    }
    
    /**
     * Gera encontros relacionados ao contrato ativo baseado na facção
     */
    private SeaEncounterType generateContractRelatedEncounter(Contract contract) {
        Faction faction = contract.getFaction();
        
        return switch (faction) {
            case GUILD -> generateGuildEncounter();
            case EMPIRE -> generateEmpireEncounter();
            case BROTHERHOOD -> generateBrotherhoodEncounter();
            case REVOLUTIONARY -> generateRevolutionaryEncounter();
        };
    }
    
    /**
     * Encontros relacionados a contratos da Guilda
     */
    private SeaEncounterType generateGuildEncounter() {
        List<SeaEncounterType> guildEncounters = List.of(
            SeaEncounterType.GUILD_CONVOY,
            SeaEncounterType.TRADE_DISPUTE,
            SeaEncounterType.MERCHANT_DISTRESS,
            // Pode incluir alguns encontros básicos relacionados
            SeaEncounterType.MERCHANT_SHIP,
            SeaEncounterType.PIRATE_VESSEL // Piratas atacando comércios
        );
        return guildEncounters.get(random.nextInt(guildEncounters.size()));
    }
    
    /**
     * Encontros relacionados a contratos do Império
     */
    private SeaEncounterType generateEmpireEncounter() {
        List<SeaEncounterType> empireEncounters = List.of(
            SeaEncounterType.IMPERIAL_ESCORT,
            SeaEncounterType.REBEL_SABOTEURS,
            SeaEncounterType.TAX_COLLECTORS,
            // Encontros básicos relacionados
            SeaEncounterType.NAVY_PATROL,
            SeaEncounterType.PIRATE_VESSEL // Rebeldes disfarçados
        );
        return empireEncounters.get(random.nextInt(empireEncounters.size()));
    }
    
    /**
     * Encontros relacionados a contratos da Irmandade
     */
    private SeaEncounterType generateBrotherhoodEncounter() {
        List<SeaEncounterType> brotherhoodEncounters = List.of(
            SeaEncounterType.SMUGGLER_MEET,
            SeaEncounterType.IMPERIAL_PURSUIT,
            SeaEncounterType.PIRATE_ALLIANCE,
            // Encontros básicos relacionados
            SeaEncounterType.PIRATE_VESSEL,
            SeaEncounterType.NAVY_PATROL // Perseguição imperial
        );
        return brotherhoodEncounters.get(random.nextInt(brotherhoodEncounters.size()));
    }
    
    /**
     * Encontros relacionados a contratos Revolucionários
     */
    private SeaEncounterType generateRevolutionaryEncounter() {
        List<SeaEncounterType> revolutionaryEncounters = List.of(
            SeaEncounterType.FREEDOM_FIGHTERS,
            SeaEncounterType.IMPERIAL_OPPRESSION,
            SeaEncounterType.UNDERGROUND_NETWORK,
            // Encontros básicos relacionados
            SeaEncounterType.NAVY_PATROL, // Opressão imperial
            SeaEncounterType.MERCHANT_SHIP // Recursos para a revolução
        );
        return revolutionaryEncounters.get(random.nextInt(revolutionaryEncounters.size()));
    }
    
    /**
     * Determina se um tipo de encontro é relacionado a combate ou narrativo
     */
    public boolean isCombatEncounter(SeaEncounterType type) {
        return switch (type) {
            // Encontros sempre de combate
            case MERCHANT_SHIP, PIRATE_VESSEL, NAVY_PATROL,
                 GUILD_CONVOY, IMPERIAL_ESCORT, REBEL_SABOTEURS,
                 TAX_COLLECTORS, SMUGGLER_MEET, IMPERIAL_PURSUIT,
                 PIRATE_ALLIANCE, FREEDOM_FIGHTERS -> true;
            
            // Encontros narrativos (podem ter opções de combate, mas não obrigatório)
            case MYSTERIOUS_WRECK, TRADE_DISPUTE, MERCHANT_DISTRESS,
                 IMPERIAL_OPPRESSION, UNDERGROUND_NETWORK -> false;
        };
    }
    
    /**
     * Determina se um encontro oferece recompensas relacionadas ao contrato
     */
    public boolean offersContractBonus(SeaEncounterType encounterType, Contract contract) {
        if (contract == null) return false;
        
        Faction contractFaction = contract.getFaction();
        
        return switch (encounterType) {
            case GUILD_CONVOY, TRADE_DISPUTE, MERCHANT_DISTRESS -> 
                contractFaction == Faction.GUILD;
            
            case IMPERIAL_ESCORT, REBEL_SABOTEURS, TAX_COLLECTORS -> 
                contractFaction == Faction.EMPIRE;
            
            case SMUGGLER_MEET, IMPERIAL_PURSUIT, PIRATE_ALLIANCE -> 
                contractFaction == Faction.BROTHERHOOD;
            
            case FREEDOM_FIGHTERS, IMPERIAL_OPPRESSION, UNDERGROUND_NETWORK -> 
                contractFaction == Faction.REVOLUTIONARY;
            
            default -> false;
        };
    }
}