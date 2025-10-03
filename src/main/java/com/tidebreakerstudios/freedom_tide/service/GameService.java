package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
import com.tidebreakerstudios.freedom_tide.dto.ResolveEventRequest;
import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.repository.ContractRepository;
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
    private final ContractRepository contractRepository;

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
                .name(request.getName()).personality(request.getPersonality()).despairLevel(request.getDespairLevel())
                .navigation(request.getNavigation()).artillery(request.getArtillery()).combat(request.getCombat())
                .medicine(request.getMedicine()).carpentry(request.getCarpentry()).intelligence(request.getIntelligence())
                .ship(ship).build();

        ship.getCrew().add(newCrewMember);
        gameRepository.save(game);
        return ship.getCrew().get(ship.getCrew().size() - 1);
    }

    @Transactional
    public Game resolveEvent(Long gameId, ResolveEventRequest request) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        EventOption chosenOption = eventOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Opção de evento não encontrada com o ID: " + request.getOptionId()));

        EventConsequence consequence = chosenOption.getConsequence();

        game.setReputation(game.getReputation() + consequence.getReputationChange());
        game.setInfamy(game.getInfamy() + consequence.getInfamyChange());
        game.setAlliance(game.getAlliance() + consequence.getAllianceChange());

        ship.setGold(ship.getGold() + consequence.getGoldChange());
        ship.setFoodRations(ship.getFoodRations() + consequence.getFoodChange());
        ship.setRumRations(ship.getRumRations() + consequence.getRumChange());

        // Lógica de moral refinada
        if (consequence.getCrewMoralChange() != 0) {
            int baseMoralChange = consequence.getCrewMoralChange();

            for (CrewMember member : ship.getCrew()) {
                double multiplier = 1.0;

                // TODO: Esta lógica deve ser expandida para considerar a natureza do evento (pró-Reputação, pró-Aliança, etc.)
                // Por enquanto, vamos assumir que um moral change negativo é "anti-ético" e um positivo é "pró-liberdade".
                if (baseMoralChange < 0) { // Ação anti-ética/pró-establishment
                    switch (member.getPersonality()) {
                        case REBEL -> multiplier = 2.0; // Odeia mais
                        case HONEST -> multiplier = 1.5; // Desaprova mais
                        case GREEDY -> multiplier = 0.5; // Se importa menos se o dinheiro for bom
                    }
                } else { // Ação pró-liberdade/ética
                    switch (member.getPersonality()) {
                        case REBEL -> multiplier = 1.5; // Gosta mais
                        case HONEST -> multiplier = 1.2; // Aprova mais
                        case GREEDY -> multiplier = 0.2; // Não se importa, não dá dinheiro
                        case BLOODTHIRSTY -> multiplier = 0.5; // Não se importa com "bondade"
                    }
                }

                int finalMoralChange = (int) (baseMoralChange * multiplier);
                member.setMoral(member.getMoral() + finalMoralChange);
            }
        }

        return gameRepository.save(game);
    }

    @Transactional
    public Game acceptContract(Long gameId, Long contractId) {
        Game game = findGameById(gameId);
        if (game.getActiveContract() != null) {
            throw new IllegalStateException("O jogo já possui um contrato ativo.");
        }

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato não encontrado com o ID: " + contractId));

        if (contract.getStatus() != ContractStatus.AVAILABLE) {
            throw new IllegalStateException("O contrato não está disponível para ser aceito.");
        }

        game.setActiveContract(contract);
        contract.setStatus(ContractStatus.IN_PROGRESS);

        contractRepository.save(contract);
        return gameRepository.save(game);
    }

    @Transactional
    public Game resolveContract(Long gameId) {
        Game game = findGameById(gameId);
        Contract activeContract = game.getActiveContract();

        if (activeContract == null) {
            throw new IllegalStateException("O jogo não possui um contrato ativo para resolver.");
        }

        Ship ship = game.getShip();

        // Apply rewards
        ship.setGold(ship.getGold() + activeContract.getRewardGold());
        game.setReputation(game.getReputation() + activeContract.getRewardReputation());
        game.setInfamy(game.getInfamy() + activeContract.getRewardInfamy());
        game.setAlliance(game.getAlliance() + activeContract.getRewardAlliance());

        // Update contract and game state
        activeContract.setStatus(ContractStatus.COMPLETED);
        game.setActiveContract(null);

        contractRepository.save(activeContract);
        return gameRepository.save(game);
    }
}