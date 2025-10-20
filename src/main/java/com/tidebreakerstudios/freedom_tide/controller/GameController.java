package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.dto.*;
import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.model.Contract;
import com.tidebreakerstudios.freedom_tide.model.CrewMember;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.service.ContractService;
import com.tidebreakerstudios.freedom_tide.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar as ações principais do jogo.
 */
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ContractService contractService; // Dependência adicionada
    private final GameMapper gameMapper;

    @PostMapping
    public ResponseEntity<GameStatusResponseDTO> createNewGame() {
        Game newGame = gameService.createNewGame();
        List<PortActionDTO> actions = gameService.getAvailablePortActions(newGame.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(gameMapper.toGameStatusResponseDTO(newGame, actions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameStatusResponseDTO> getGameById(@PathVariable Long id) {
        GameStatusResponseDTO gameStatus = gameService.getGameStatusDTO(id);
        return ResponseEntity.ok(gameStatus);
    }

    @GetMapping("/{gameId}/port")
    public ResponseEntity<PortDTO> getCurrentPort(@PathVariable Long gameId) {
        PortDTO portDTO = gameService.getCurrentPort(gameId);
        return ResponseEntity.ok(portDTO);
    }

    @GetMapping("/{gameId}/port/actions")
    public ResponseEntity<List<PortActionDTO>> getPortActions(@PathVariable Long gameId) {
        List<PortActionDTO> actions = gameService.getAvailablePortActions(gameId);
        return ResponseEntity.ok(actions);
    }

    @PostMapping("/{gameId}/travel")
    public ResponseEntity<GameActionResponseDTO> travelToPort(
            @PathVariable Long gameId,
            @Valid @RequestBody TravelRequestDTO request) {
        GameActionResponseDTO response = gameService.travelToPort(gameId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameId}/travel/destinations")
    public ResponseEntity<List<PortSummaryDTO>> getTravelDestinations(@PathVariable Long gameId) {
        List<PortSummaryDTO> destinations = gameService.getTravelDestinations(gameId);
        return ResponseEntity.ok(destinations);
    }

    // --- Endpoints de Encontro ---

    @GetMapping("/{gameId}/encounter/actions")
    public ResponseEntity<List<EncounterActionDTO>> getEncounterActions(@PathVariable Long gameId) {
        List<EncounterActionDTO> actions = gameService.getAvailableEncounterActions(gameId);
        return ResponseEntity.ok(actions);
    }

    @PostMapping("/{gameId}/encounter/flee")
    public ResponseEntity<GameActionResponseDTO> fleeEncounter(@PathVariable Long gameId) {
        GameActionResponseDTO response = gameService.fleeEncounter(gameId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/encounter/investigate")
    public ResponseEntity<GameActionResponseDTO> investigateEncounter(@PathVariable Long gameId) {
        GameActionResponseDTO response = gameService.investigateEncounter(gameId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/encounter/attack")
    public ResponseEntity<GameActionResponseDTO> attackEncounter(@PathVariable Long gameId) {
        GameActionResponseDTO response = gameService.attackEncounter(gameId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/encounter/board")
    public ResponseEntity<GameActionResponseDTO> boardEncounter(@PathVariable Long gameId) {
        GameActionResponseDTO response = gameService.boardEncounter(gameId);
        return ResponseEntity.ok(response);
    }

    // --- Endpoints de Porto ---

    @GetMapping("/{gameId}/port/shipyard")
    public ResponseEntity<ShipyardDTO> getShipyardInfo(@PathVariable Long gameId) {
        ShipyardDTO shipyardDTO = gameService.getShipyardInfo(gameId);
        return ResponseEntity.ok(shipyardDTO);
    }

    @PostMapping("/{gameId}/port/shipyard/repair")
    public ResponseEntity<GameActionResponseDTO> repairShip(@PathVariable Long gameId) {
        GameActionResponseDTO response = gameService.repairShip(gameId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/port/shipyard/upgrades/{upgradeId}")
    public ResponseEntity<GameActionResponseDTO> purchaseUpgrade(
            @PathVariable Long gameId,
            @PathVariable Long upgradeId) {
        GameActionResponseDTO response = gameService.purchaseUpgrade(gameId, upgradeId);
        return ResponseEntity.ok(response);
    }

    // --- Endpoints de Mercado ---

    @GetMapping("/{gameId}/port/market")
    public ResponseEntity<MarketDTO> getMarketInfo(@PathVariable Long gameId) {
        MarketDTO marketDTO = gameService.getMarketInfo(gameId);
        return ResponseEntity.ok(marketDTO);
    }

    @PostMapping("/{gameId}/port/market/buy")
    public ResponseEntity<GameActionResponseDTO> buyMarketItem(
            @PathVariable Long gameId,
            @Valid @RequestBody MarketTransactionRequest request) {
        GameActionResponseDTO response = gameService.buyMarketItem(gameId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/port/market/sell")
    public ResponseEntity<GameActionResponseDTO> sellMarketItem(
            @PathVariable Long gameId,
            @Valid @RequestBody MarketTransactionRequest request) {
        GameActionResponseDTO response = gameService.sellMarketItem(gameId, request);
        return ResponseEntity.ok(response);
    }

    // --- Endpoints de Taverna ---

    @GetMapping("/{gameId}/port/tavern")
    public ResponseEntity<List<TavernRecruitDTO>> getTavernRecruits(@PathVariable Long gameId) {
        List<TavernRecruitDTO> recruits = gameService.getTavernRecruits(gameId);
        return ResponseEntity.ok(recruits);
    }

    @PostMapping("/{gameId}/ship/crew")
    public ResponseEntity<CrewMemberResponseDTO> recruitCrewMember(
            @PathVariable Long gameId,
            @Valid @RequestBody RecruitCrewMemberRequest request) {
        CrewMember newCrewMember = gameService.recruitCrewMember(gameId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(gameMapper.toCrewMemberResponseDTO(newCrewMember));
    }

    /**
     * Endpoint para o jogador resolver um evento, escolhendo uma opção.
     * @param gameId O ID do jogo atual.
     * @param request O DTO com o ID da opção escolhida.
     * @return ResponseEntity com o estado do jogo atualizado e um log de eventos.
     */
    @PostMapping("/{gameId}/resolve-event")
    public ResponseEntity<GameActionResponseDTO> resolveEvent(
            @PathVariable Long gameId,
            @Valid @RequestBody ResolveEventRequest request) {
        GameActionResponseDTO response = gameService.resolveEvent(gameId, request);
        return ResponseEntity.ok(response);
    }

    // --- Endpoints de Contrato ---

    @GetMapping("/{gameId}/contracts")
    public ResponseEntity<List<ContractDTO>> getAvailableContractsForGame(@PathVariable Long gameId) {
        List<Contract> contracts = contractService.getAvailableContractsForGame(gameId);
        List<ContractDTO> contractDTOs = contracts.stream()
                .map(gameMapper::toContractDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contractDTOs);
    }

    @PostMapping("/{gameId}/contracts/{contractId}/accept")
    public ResponseEntity<GameStatusResponseDTO> acceptContract(
            @PathVariable Long gameId,
            @PathVariable Long contractId) {
        Game updatedGame = gameService.acceptContract(gameId, contractId);
        return ResponseEntity.ok(gameMapper.toGameStatusResponseDTO(updatedGame));
    }

    @PostMapping("/{gameId}/contracts/resolve")
    public ResponseEntity<GameActionResponseDTO> resolveContract(@PathVariable Long gameId) {
        GameActionResponseDTO response = gameService.resolveContract(gameId);
        return ResponseEntity.ok(response);
    }
}