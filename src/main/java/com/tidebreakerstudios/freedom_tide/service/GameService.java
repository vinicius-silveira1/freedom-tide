package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
import com.tidebreakerstudios.freedom_tide.dto.ResolveEventRequest;
import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.repository.EventOptionRepository;
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
    private final EventOptionRepository eventOptionRepository;

    @Transactional
    public Game createNewGame() {
        Ship newShip = Ship.builder().name("O Andarilho").type(ShipType.SCHOONER).crew(new ArrayList<>()).build();
        Game newGame = Game.builder().ship(newShip).build();
        newShip.setGame(newGame);
        return gameRepository.save(newGame);
    }

    @Transactional(readOnly = true)
    public Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + id));
    }

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
        gameRepository.save(game);
        return ship.getCrew().get(ship.getCrew().size() - 1);
    }

    /**
     * Processa a escolha de um jogador para um evento narrativo e aplica as consequências.
     * @param gameId O ID do jogo atual.
     * @param request O DTO contendo o ID da opção escolhida.
     * @return O estado do jogo atualizado após as consequências.
     */
    @Transactional
    public Game resolveEvent(Long gameId, ResolveEventRequest request) {
        // 1. Busca as entidades necessárias
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        EventOption chosenOption = eventOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Opção de evento não encontrada com o ID: " + request.getOptionId()));

        // 2. Pega o objeto de consequências
        EventConsequence consequence = chosenOption.getConsequence();

        // 3. Aplica as consequências ao estado do jogo e do navio
        game.setReputation(game.getReputation() + consequence.getReputationChange());
        game.setInfamy(game.getInfamy() + consequence.getInfamyChange());
        game.setAlliance(game.getAlliance() + consequence.getAllianceChange());

        ship.setGold(ship.getGold() + consequence.getGoldChange());
        ship.setFoodRations(ship.getFoodRations() + consequence.getFoodChange());
        ship.setRumRations(ship.getRumRations() + consequence.getRumChange());

        // 4. Aplica a mudança de moral à tripulação (aqui podemos adicionar lógicas mais complexas no futuro)
        if (consequence.getCrewMoralChange() != 0) {
            for (CrewMember member : ship.getCrew()) {
                member.setMoral(member.getMoral() + consequence.getCrewMoralChange());
                // TODO: Adicionar lógica futura para que a mudança de moral varie com a personalidade do tripulante.
            }
        }

        // 5. Salva e retorna o estado do jogo atualizado
        return gameRepository.save(game);
    }
}
