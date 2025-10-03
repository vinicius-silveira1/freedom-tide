package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
import com.tidebreakerstudios.freedom_tide.model.CrewMember;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.Ship;
import com.tidebreakerstudios.freedom_tide.model.ShipType;
import com.tidebreakerstudios.freedom_tide.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
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
        Ship newShip = Ship.builder()
                .name("O Andarilho")
                .type(ShipType.SCHOONER) // O navio mais básico
                .crew(new ArrayList<>())
                .build();

        Game newGame = Game.builder()
                .ship(newShip)
                .build();

        newShip.setGame(newGame);

        return gameRepository.save(newGame);
    }

    /**
     * Busca uma sessão de jogo pelo seu ID.
     * @param id O ID do jogo a ser buscado.
     * @return A entidade Game encontrada.
     * @throws EntityNotFoundException se nenhum jogo for encontrado com o ID fornecido.
     */
    @Transactional(readOnly = true)
    public Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + id));
    }

    /**
     * Recruta um novo tripulante e o adiciona ao navio de um jogo existente.
     * @param gameId O ID do jogo ao qual o tripulante será adicionado.
     * @param request O DTO com os dados do tripulante a ser recrutado.
     * @return A entidade CrewMember recém-criada e persistida, agora com seu ID.
     */
    @Transactional
    public CrewMember recruitCrewMember(Long gameId, RecruitCrewMemberRequest request) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();

        CrewMember newCrewMember = CrewMember.builder()
                .name(request.getName())
                .personality(request.getPersonality())
                .despairLevel(request.getDespairLevel())
                .navigation(request.getNavigation())
                .artillery(request.getArtillery())
                .combat(request.getCombat())
                .medicine(request.getMedicine())
                .carpentry(request.getCarpentry())
                .intelligence(request.getIntelligence())
                .ship(ship)
                .build();

        ship.getCrew().add(newCrewMember);

        // Salvar a entidade pai (Game) persiste o novo CrewMember por cascata.
        gameRepository.save(game);

        // Retorna o último tripulante da lista, que agora garantidamente possui o ID do banco.
        return ship.getCrew().get(ship.getCrew().size() - 1);
    }
}