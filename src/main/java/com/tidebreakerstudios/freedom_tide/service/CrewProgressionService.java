package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.CrewMember;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewProfession;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewRank;
import com.tidebreakerstudios.freedom_tide.repository.CrewMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por gerenciar a progressão e evolução da tripulação.
 * Implementa as mecânicas de XP, evolução de ranks e habilidades especiais.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrewProgressionService {

    private final CrewMemberRepository crewMemberRepository;

    // Constantes de XP por ação
    private static final int XP_NAVIGATION = 15;
    private static final int XP_COMPLETE_JOURNEY = 25;
    private static final int XP_SURVIVE_STORM = 50;
    private static final int XP_NAVAL_COMBAT = 20;
    private static final int XP_WIN_COMBAT = 40;
    private static final int XP_CRITICAL_HIT = 30;
    private static final int XP_BOARDING_ACTION = 25;
    private static final int XP_INTIMIDATION = 15;
    private static final int XP_PROTECT_CREW = 35;
    private static final int XP_HEAL_CREW = 20;
    private static final int XP_PREVENT_DISEASE = 30;
    private static final int XP_SAVE_LIFE = 60;
    private static final int XP_REPAIR_SHIP = 15;
    private static final int XP_SHIP_UPGRADE = 25;
    private static final int XP_SURVIVE_LOW_HULL = 40;
    private static final int XP_COMPLETE_CONTRACT = 20;
    private static final int XP_SUCCESSFUL_NEGOTIATION = 25;
    private static final int XP_SOLVE_PUZZLE = 35;

    /**
     * Concede XP para navegadores quando o navio viaja.
     */
    @Transactional
    public List<String> awardNavigationXP(Game game) {
        List<String> progressMessages = new ArrayList<>();
        List<CrewMember> navigators = getCrewByProfession(game, CrewProfession.NAVIGATOR, CrewProfession.EXPLORER);
        
        for (CrewMember navigator : navigators) {
            boolean rankedUp = navigator.gainExperience(XP_NAVIGATION);
            navigator.setJourneysCompleted(navigator.getJourneysCompleted() + 1);
            
            if (rankedUp) {
                progressMessages.add(String.format("🧭 %s evoluiu para %s!", 
                    navigator.getName(), navigator.getRank().getDisplayName()));
                unlockNavigationAbilities(navigator);
            }
        }
        
        if (!navigators.isEmpty()) {
            crewMemberRepository.saveAll(navigators);
        }
        
        return progressMessages;
    }

    /**
     * Concede XP para artilheiros em combate naval.
     */
    @Transactional
    public List<String> awardCombatXP(Game game, boolean victory, boolean criticalHit) {
        List<String> progressMessages = new ArrayList<>();
        List<CrewMember> combatCrew = getCrewByProfession(game, 
            CrewProfession.GUNNER, CrewProfession.FIGHTER, CrewProfession.CORSAIR);
        
        for (CrewMember member : combatCrew) {
            int xpGained = XP_NAVAL_COMBAT;
            if (victory) xpGained += XP_WIN_COMBAT;
            if (criticalHit && (member.getProfession() == CrewProfession.GUNNER || 
                               member.getProfession() == CrewProfession.CORSAIR)) {
                xpGained += XP_CRITICAL_HIT;
            }
            
            boolean rankedUp = member.gainExperience(xpGained);
            member.setCombatsParticipated(member.getCombatsParticipated() + 1);
            
            if (rankedUp) {
                progressMessages.add(String.format("⚔️ %s evoluiu para %s!", 
                    member.getName(), member.getRank().getDisplayName()));
                unlockCombatAbilities(member);
            }
        }
        
        if (!combatCrew.isEmpty()) {
            crewMemberRepository.saveAll(combatCrew);
        }
        
        return progressMessages;
    }

    /**
     * Concede XP para médicos quando curam a tripulação.
     */
    @Transactional
    public List<String> awardMedicalXP(Game game, boolean savedLife) {
        List<String> progressMessages = new ArrayList<>();
        List<CrewMember> medics = getCrewByProfession(game, CrewProfession.MEDIC, CrewProfession.BATTLE_MEDIC);
        
        for (CrewMember medic : medics) {
            int xpGained = savedLife ? XP_SAVE_LIFE : XP_HEAL_CREW;
            boolean rankedUp = medic.gainExperience(xpGained);
            medic.setHealingsPerformed(medic.getHealingsPerformed() + 1);
            
            if (rankedUp) {
                progressMessages.add(String.format("🏥 %s evoluiu para %s!", 
                    medic.getName(), medic.getRank().getDisplayName()));
                unlockMedicalAbilities(medic);
            }
        }
        
        if (!medics.isEmpty()) {
            crewMemberRepository.saveAll(medics);
        }
        
        return progressMessages;
    }

    /**
     * Concede XP para carpinteiros quando reparam o navio.
     */
    @Transactional
    public List<String> awardRepairXP(Game game, boolean emergencyRepair) {
        List<String> progressMessages = new ArrayList<>();
        List<CrewMember> carpenters = getCrewByProfession(game, CrewProfession.CARPENTER);
        
        for (CrewMember carpenter : carpenters) {
            int xpGained = emergencyRepair ? XP_SURVIVE_LOW_HULL : XP_REPAIR_SHIP;
            boolean rankedUp = carpenter.gainExperience(xpGained);
            carpenter.setRepairsPerformed(carpenter.getRepairsPerformed() + 1);
            
            if (rankedUp) {
                progressMessages.add(String.format("🔨 %s evoluiu para %s!", 
                    carpenter.getName(), carpenter.getRank().getDisplayName()));
                unlockCarpenterAbilities(carpenter);
            }
        }
        
        if (!carpenters.isEmpty()) {
            crewMemberRepository.saveAll(carpenters);
        }
        
        return progressMessages;
    }

    /**
     * Concede XP para estrategistas quando completam contratos.
     */
    @Transactional
    public List<String> awardStrategicXP(Game game, boolean negotiationSuccess) {
        List<String> progressMessages = new ArrayList<>();
        List<CrewMember> strategists = getCrewByProfession(game, CrewProfession.STRATEGIST, CrewProfession.EXPLORER);
        
        for (CrewMember strategist : strategists) {
            int xpGained = XP_COMPLETE_CONTRACT;
            if (negotiationSuccess) xpGained += XP_SUCCESSFUL_NEGOTIATION;
            
            boolean rankedUp = strategist.gainExperience(xpGained);
            
            if (rankedUp) {
                progressMessages.add(String.format("🧠 %s evoluiu para %s!", 
                    strategist.getName(), strategist.getRank().getDisplayName()));
                unlockStrategistAbilities(strategist);
            }
        }
        
        if (!strategists.isEmpty()) {
            crewMemberRepository.saveAll(strategists);
        }
        
        return progressMessages;
    }

    /**
     * Atualiza as profissões de toda a tripulação baseado nos atributos atuais.
     */
    @Transactional
    public void updateAllProfessions(Game game) {
        List<CrewMember> crew = game.getShip().getCrew();
        boolean anyChanges = false;
        
        for (CrewMember member : crew) {
            CrewProfession oldProfession = member.getProfession();
            member.updateProfession();
            
            if (oldProfession != member.getProfession()) {
                log.info("Tripulante {} mudou de profissão: {} -> {}", 
                    member.getName(), oldProfession, member.getProfession());
                anyChanges = true;
            }
        }
        
        if (anyChanges) {
            crewMemberRepository.saveAll(crew);
        }
    }

    // === MÉTODOS PRIVADOS ===

    private List<CrewMember> getCrewByProfession(Game game, CrewProfession... professions) {
        return game.getShip().getCrew().stream()
            .filter(member -> {
                for (CrewProfession profession : professions) {
                    if (member.getProfession() == profession) return true;
                }
                return false;
            })
            .toList();
    }

    private void unlockNavigationAbilities(CrewMember navigator) {
        switch (navigator.getRank()) {
            case NAVIGATOR_SAILOR -> navigator.unlockAbility("REDUCE_TRAVEL_TIME");
            case NAVIGATOR_QUARTERMASTER -> navigator.unlockAbility("AVOID_STORMS");
            case NAVIGATOR_CAPTAIN -> navigator.unlockAbility("SECRET_ROUTES");
            case NAVIGATOR_LEGEND -> navigator.unlockAbility("MASTER_NAVIGATOR");
        }
    }

    private void unlockCombatAbilities(CrewMember fighter) {
        switch (fighter.getRank()) {
            case GUNNER_CANNON, FIGHTER_VETERAN -> fighter.unlockAbility("INCREASED_DAMAGE");
            case GUNNER_MASTER, FIGHTER_ELITE -> fighter.unlockAbility("SPECIAL_AMMUNITION");
            case GUNNER_DEMON, FIGHTER_LEGEND -> fighter.unlockAbility("PHANTOM_SHOT");
        }
    }

    private void unlockMedicalAbilities(CrewMember medic) {
        switch (medic.getRank()) {
            case MEDIC_SURGEON -> medic.unlockAbility("MORALE_RECOVERY");
            case MEDIC_DOCTOR -> medic.unlockAbility("CURE_DISEASES");
            case MEDIC_MIRACLE -> medic.unlockAbility("RESURRECTION");
        }
    }

    private void unlockCarpenterAbilities(CrewMember carpenter) {
        switch (carpenter.getRank()) {
            case CARPENTER_CRAFTSMAN -> carpenter.unlockAbility("AUTO_REPAIR");
            case CARPENTER_MASTER -> carpenter.unlockAbility("SHIP_UPGRADES");
            case CARPENTER_LEGEND -> carpenter.unlockAbility("GHOST_SHIP");
        }
    }

    private void unlockStrategistAbilities(CrewMember strategist) {
        switch (strategist.getRank()) {
            case STRATEGIST_ADVISOR -> strategist.unlockAbility("INCREASED_PROFITS");
            case STRATEGIST_MASTER -> strategist.unlockAbility("PREDICT_AMBUSH");
            case STRATEGIST_GENIUS -> strategist.unlockAbility("FORCE_SURRENDER");
        }
    }
}