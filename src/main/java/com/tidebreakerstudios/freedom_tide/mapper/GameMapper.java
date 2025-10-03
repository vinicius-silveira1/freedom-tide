package com.tidebreakerstudios.freedom_tide.mapper;

import com.tidebreakerstudios.freedom_tide.dto.CaptainCompassDTO;
import com.tidebreakerstudios.freedom_tide.dto.CrewSummaryDTO;
import com.tidebreakerstudios.freedom_tide.dto.GameStatusResponseDTO;
import com.tidebreakerstudios.freedom_tide.dto.ShipSummaryDTO;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.Ship;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameMapper {

    public GameStatusResponseDTO toGameStatusResponseDTO(Game game) {
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

        List<String> upgrades = new ArrayList<>();
        if (ship.isHasPrintingPress()) {
            upgrades.add("Printing Press");
        }
        if (ship.isHasSmugglingCompartments()) {
            upgrades.add("Smuggling Compartments");
        }
        if (ship.isHasLuxuryCabin()) {
            upgrades.add("Luxury Cabin");
        }

        ShipSummaryDTO shipDTO = ShipSummaryDTO.builder()
                .name(ship.getName())
                .type(ship.getType().name())
                .hullIntegrity(ship.getHullIntegrity())
                .gold(ship.getGold())
                .foodRations(ship.getFoodRations())
                .rumRations(ship.getRumRations())
                .repairParts(ship.getRepairParts())
                .cannonballs(ship.getCannonballs())
                .upgrades(upgrades)
                .build();

        return GameStatusResponseDTO.builder()
                .id(game.getId())
                .captainCompass(compassDTO)
                .ship(shipDTO)
                .crew(crewDTO)
                .build();
    }
}
