package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
