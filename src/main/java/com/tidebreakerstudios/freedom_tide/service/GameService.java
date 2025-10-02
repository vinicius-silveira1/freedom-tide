package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.Ship;
import com.tidebreakerstudios.freedom_tide.model.ShipType;
import com.tidebreakerstudios.freedom_tide.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Camada de serviço para gerenciar a lógica de negócio principal do jogo.
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    /**
     * Cria uma nova sessão de jogo, com um navio inicial e estado padrão.
     * @return A entidade Game recém-criada e persistida.
     */
    @Transactional
    public Game createNewGame() {
        // 1. Cria o navio inicial com atributos neutros e sem tripulação.
        Ship newShip = Ship.builder()
                .name("O Andarilho")
                .type(ShipType.SCHOONER) // O navio mais básico
                .crew(new ArrayList<>()) // Começa sem tripulação
                .build();

        // 2. Cria a nova sessão de jogo com os valores padrão da Bússola do Capitão.
        Game newGame = Game.builder()
                .ship(newShip)
                .build();

        // 3. Estabelece a relação bidirecional.
        newShip.setGame(newGame);

        // 4. Salva o jogo (o navio será salvo em cascata) e o retorna.
        return gameRepository.save(newGame);
    }
}
