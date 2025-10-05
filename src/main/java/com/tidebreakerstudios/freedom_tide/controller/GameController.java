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
        return ResponseEntity.status(HttpStatus.CREATED).body(gameMapper.toGameStatusResponseDTO(newGame));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameStatusResponseDTO> getGameById(@PathVariable Long id) {
        Game game = gameService.findGameById(id);
        return ResponseEntity.ok(gameMapper.toGameStatusResponseDTO(game));
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
    public ResponseEntity<GameStatusResponseDTO> travelToPort(
            @PathVariable Long gameId,
            @Valid @RequestBody TravelRequestDTO request) {
        Game updatedGame = gameService.travelToPort(gameId, request);
        return ResponseEntity.ok(gameMapper.toGameStatusResponseDTO(updatedGame));
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