package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.dto.GameStatusResponseDTO;
import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
import com.tidebreakerstudios.freedom_tide.dto.ResolveEventRequest;
import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.model.CrewMember;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciar as ações principais do jogo.
 */
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
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

    @PostMapping("/{gameId}/ship/crew")
    public ResponseEntity<CrewMember> recruitCrewMember(
            @PathVariable Long gameId,
            @Valid @RequestBody RecruitCrewMemberRequest request) {
        CrewMember newCrewMember = gameService.recruitCrewMember(gameId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCrewMember);
    }

    /**
     * Endpoint para o jogador resolver um evento, escolhendo uma opção.
     * @param gameId O ID do jogo atual.
     * @param request O DTO com o ID da opção escolhida.
     * @return ResponseEntity com o estado do jogo atualizado após as consequências.
     */
    @PostMapping("/{gameId}/resolve-event")
    public ResponseEntity<GameStatusResponseDTO> resolveEvent(
            @PathVariable Long gameId,
            @Valid @RequestBody ResolveEventRequest request) {
        Game updatedGame = gameService.resolveEvent(gameId, request);
        return ResponseEntity.ok(gameMapper.toGameStatusResponseDTO(updatedGame));
    }
}