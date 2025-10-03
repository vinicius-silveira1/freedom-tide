package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
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

    /**
     * Cria uma nova sessão de jogo.
     * @return ResponseEntity com o novo jogo criado e o status HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Game> createNewGame() {
        Game newGame = gameService.createNewGame();
        return ResponseEntity.status(HttpStatus.CREATED).body(newGame);
    }

    /**
     * Busca um jogo pelo seu ID.
     * @param id O ID do jogo a ser buscado, extraído da URL.
     * @return ResponseEntity com o jogo encontrado e o status HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = gameService.findGameById(id);
        return ResponseEntity.ok(game);
    }

    /**
     * Recruta um novo tripulante para o navio de um jogo específico.
     * @param gameId O ID do jogo.
     * @param request O DTO contendo os dados do novo tripulante.
     * @return ResponseEntity com o tripulante recém-criado e o status HTTP 201 (Created).
     */
    @PostMapping("/{gameId}/ship/crew")
    public ResponseEntity<CrewMember> recruitCrewMember(
            @PathVariable Long gameId,
            @Valid @RequestBody RecruitCrewMemberRequest request) {
        CrewMember newCrewMember = gameService.recruitCrewMember(gameId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCrewMember);
    }
}
