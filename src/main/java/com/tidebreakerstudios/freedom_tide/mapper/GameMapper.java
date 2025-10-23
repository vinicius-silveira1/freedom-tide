package com.tidebreakerstudios.freedom_tide.mapper;

import com.tidebreakerstudios.freedom_tide.dto.*;
import com.tidebreakerstudios.freedom_tide.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameMapper {

    public GameStatusResponseDTO toGameStatusResponseDTO(Game game) {
        return toGameStatusResponseDTO(game, List.of(), List.of());
    }

    public GameStatusResponseDTO toGameStatusResponseDTO(Game game, List<PortActionDTO> portActions) {
        return toGameStatusResponseDTO(game, portActions, List.of());
    }

    public GameStatusResponseDTO toGameStatusResponseDTO(Game game, List<PortActionDTO> portActions, List<EncounterActionDTO> encounterActions) {
        if (game == null) {
            return null;
        }

        Ship ship = game.getShip();

        CaptainCompassDTO compassDTO = CaptainCompassDTO.builder()
                .reputation(game.getReputation())
                .infamy(game.getInfamy())
                .alliance(game.getAlliance())
                .build();

        CrewSummaryDTO crewDTO = CrewSummaryDTO.builder()
                .crewCount(ship.getCrew().size())
                .averageMorale(ship.getAverageMorale())
                .build();

        ShipSummaryDTO shipDTO = ShipSummaryDTO.builder()
                .name(ship.getName())
                .type(ship.getType().name())
                .hullIntegrity(ship.getHullIntegrity())
                .gold(game.getGold())
                .foodRations(ship.getFoodRations())
                .rumRations(ship.getRumRations())
                .repairParts(ship.getRepairParts())
                .shot(ship.getShot())
                .upgrades(toShipUpgradeDTOList(ship.getUpgrades())) // Use new mapping
                .build();

        return GameStatusResponseDTO.builder()
                .id(game.getId())
                .captainCompass(compassDTO)
                .ship(shipDTO)
                .crew(crewDTO)
                .activeContract(toContractDTO(game.getActiveContract()))
                .currentPort(toPortDTO(game.getCurrentPort(), portActions))
                .currentEncounter(toSeaEncounterDTO(game.getCurrentEncounter(), encounterActions))
                .gameOver(game.isGameOver())
                .gameOverReason(game.getGameOverReason())
                .build();
    }

    public List<ShipUpgradeDTO> toShipUpgradeDTOList(List<ShipUpgrade> upgrades) {
        if (upgrades == null) {
            return List.of();
        }
        return upgrades.stream()
                .map(this::toShipUpgradeDTO)
                .collect(Collectors.toList());
    }

    public ShipUpgradeDTO toShipUpgradeDTO(ShipUpgrade upgrade) {
        if (upgrade == null) {
            return null;
        }
        return new ShipUpgradeDTO(
                upgrade.getId(),
                upgrade.getName(),
                upgrade.getDescription(),
                upgrade.getType(),
                upgrade.getModifier(),
                upgrade.getCost()
        );
    }

    public CrewMemberResponseDTO toCrewMemberResponseDTO(CrewMember crewMember) {
        if (crewMember == null) {
            return null;
        }

        CrewAttributesDTO attributesDTO = CrewAttributesDTO.builder()
                .navigation(crewMember.getNavigation())
                .artillery(crewMember.getArtillery())
                .combat(crewMember.getCombat())
                .medicine(crewMember.getMedicine())
                .carpentry(crewMember.getCarpentry())
                .intelligence(crewMember.getIntelligence())
                .build();

        return CrewMemberResponseDTO.builder()
                .id(crewMember.getId())
                .name(crewMember.getName())
                .personality(crewMember.getPersonality().name())
                .moral(crewMember.getMoral())
                .loyalty(crewMember.getLoyalty())
                .salary(crewMember.getSalary())
                .attributes(attributesDTO)
                .build();
    }

    public ContractDTO toContractDTO(Contract contract) {
        if (contract == null) {
            return null;
        }

        return ContractDTO.builder()
                .id(contract.getId())
                .title(contract.getTitle())
                .description(contract.getDescription())
                .faction(contract.getFaction())
                .rewardGold(contract.getRewardGold())
                .rewardReputation(contract.getRewardReputation())
                .rewardInfamy(contract.getRewardInfamy())
                .rewardAlliance(contract.getRewardAlliance())
                .build();
    }

    public PortDTO toPortDTO(com.tidebreakerstudios.freedom_tide.model.Port port, List<PortActionDTO> availableActions) {
        if (port == null) {
            return null;
        }

        return new PortDTO(
                port.getId(),
                port.getName(),
                port.getType().name(),
                availableActions
        );
    }

    public PortDTO toPortDTO(com.tidebreakerstudios.freedom_tide.model.Port port) {
        return toPortDTO(port, List.of());
    }

    public SeaEncounterDTO toSeaEncounterDTO(com.tidebreakerstudios.freedom_tide.model.SeaEncounter encounter) {
        return toSeaEncounterDTO(encounter, List.of());
    }

    public SeaEncounterDTO toSeaEncounterDTO(com.tidebreakerstudios.freedom_tide.model.SeaEncounter encounter, List<EncounterActionDTO> availableActions) {
        if (encounter == null) {
            return null;
        }

        return new SeaEncounterDTO(
                encounter.getId(),
                encounter.getDescription(),
                encounter.getType(),
                availableActions
        );
    }
}