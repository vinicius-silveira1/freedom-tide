package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.GameActionResponseDTO;
import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
import com.tidebreakerstudios.freedom_tide.dto.ResolveEventRequest;
import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.repository.ContractRepository;
import com.tidebreakerstudios.freedom_tide.repository.EventOptionRepository;
import com.tidebreakerstudios.freedom_tide.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Camada de serviço para gerenciar a lógica de negócio principal do jogo.
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final EventOptionRepository eventOptionRepository;
    private final ContractRepository contractRepository;
    private final GameMapper gameMapper; // Injetado para conversão de DTO

    // Constantes de Moral
    private static final int INSUBORDINATION_THRESHOLD = 30;
    private static final int DISCONTENTMENT_THRESHOLD = 50;
    private static final double INSUBORDINATION_CHANCE = 0.4; // 40% de chance
    private static final double DISCONTENTMENT_CHANCE = 0.3; // 30% de chance
    private static final double GOLD_PENALTY_PERCENTAGE = 0.15; // 15% de perda
    private static final double RESOURCE_LOSS_PERCENTAGE = 0.10; // 10% de perda

    // Constantes de Fim de Turno
    private static final int FOOD_CONSUMPTION_PER_CREW = 2;
    private static final int RUM_CONSUMPTION_PER_CREW = 1;
    private static final int MORALE_PENALTY_NO_FOOD = -15;
    private static final int MORALE_PENALTY_NO_RUM = -10;
    private static final int MORALE_PENALTY_NO_PAY = -20;


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

        // Calcular moral inicial dinamicamente
        int baseMoral = 50;
        int personalityModifier = request.getPersonality().getMoralModifier() * 5;
        int despairPenalty = request.getDespairLevel() * 2;
        int initialMoral = baseMoral + personalityModifier - despairPenalty;
        newCrewMember.setMoral(Math.max(0, Math.min(100, initialMoral))); // Garante que a moral fique entre 0 e 100

        // Calcular salário dinamicamente
        int attributeSum = request.getNavigation() + request.getArtillery() + request.getCombat() +
                           request.getMedicine() + request.getCarpentry() + request.getIntelligence();
        int baseSalary = 10;
        int salary = baseSalary + (attributeSum / 10) - request.getDespairLevel();
        newCrewMember.setSalary(Math.max(1, salary)); // Garante um salário mínimo de 1

        ship.getCrew().add(newCrewMember);
        gameRepository.save(game);
        return ship.getCrew().get(ship.getCrew().size() - 1);
    }

    @Transactional
    public GameActionResponseDTO resolveEvent(Long gameId, ResolveEventRequest request) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();

        EventOption chosenOption = eventOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Opção de evento não encontrada com o ID: " + request.getOptionId()));

        eventLog.add("Evento resolvido: " + chosenOption.getNarrativeEvent().getTitle());
        EventConsequence consequence = chosenOption.getConsequence();

        // Aplicar consequências
        game.setReputation(game.getReputation() + consequence.getReputationChange());
        game.setInfamy(game.getInfamy() + consequence.getInfamyChange());
        game.setAlliance(game.getAlliance() + consequence.getAllianceChange());
        ship.setFoodRations(ship.getFoodRations() + consequence.getFoodChange());
        ship.setRumRations(ship.getRumRations() + consequence.getRumChange());

        // Processar ouro e aplicar consequências de moral
        handleMoraleConsequences(ship, consequence.getGoldChange(), eventLog);

        // Lógica de moral refinada
        if (consequence.getCrewMoralChange() != 0) {
            int baseMoralChange = consequence.getCrewMoralChange();
            for (CrewMember member : ship.getCrew()) {
                double multiplier = 1.0;
                if (baseMoralChange < 0) {
                    switch (member.getPersonality()) {
                        case REBEL -> multiplier = 2.0;
                        case HONEST -> multiplier = 1.5;
                        case GREEDY -> multiplier = 0.5;
                    }
                } else {
                    switch (member.getPersonality()) {
                        case REBEL -> multiplier = 1.5;
                        case HONEST -> multiplier = 1.2;
                        case GREEDY -> multiplier = 0.2;
                        case BLOODTHIRSTY -> multiplier = 0.5;
                    }
                }
                int finalMoralChange = (int) (baseMoralChange * multiplier);
                member.setMoral(member.getMoral() + finalMoralChange);
            }
            eventLog.add(String.format("A moral da tripulação mudou em resposta às suas ações."));
        }

        // Finalizar o ciclo de turno (consumo e pagamento)
        endTurnCycle(ship, eventLog);

        Game savedGame = gameRepository.save(game);

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                .eventLog(eventLog)
                .build();
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
    public GameActionResponseDTO resolveContract(Long gameId) {
        Game game = findGameById(gameId);
        Contract activeContract = game.getActiveContract();
        List<String> eventLog = new ArrayList<>();

        if (activeContract == null) {
            throw new IllegalStateException("O jogo não possui um contrato ativo para resolver.");
        }

        eventLog.add("Contrato concluído: " + activeContract.getTitle());
        Ship ship = game.getShip();

        // Processar ouro e aplicar consequências de moral
        handleMoraleConsequences(ship, activeContract.getRewardGold(), eventLog);

        // Aplicar outras recompensas
        game.setReputation(game.getReputation() + activeContract.getRewardReputation());
        game.setInfamy(game.getInfamy() + activeContract.getRewardInfamy());
        game.setAlliance(game.getAlliance() + activeContract.getRewardAlliance());

        // Finalizar o ciclo de turno (consumo e pagamento)
        endTurnCycle(ship, eventLog);

        // Atualizar estado do contrato e do jogo
        activeContract.setStatus(ContractStatus.COMPLETED);
        game.setActiveContract(null);

        contractRepository.save(activeContract);
        Game savedGame = gameRepository.save(game);

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                .eventLog(eventLog)
                .build();
    }

    private void handleMoraleConsequences(Ship ship, int goldReward, List<String> eventLog) {
        double averageMorale = ship.getAverageMorale();

        // Estágio 2: Insubordinação (moral < 30)
        if (averageMorale < INSUBORDINATION_THRESHOLD) {
            if (ThreadLocalRandom.current().nextDouble() < INSUBORDINATION_CHANCE) {
                applyInsubordinationPenalty(ship, eventLog);
                // Se a insubordinação ocorrer, não aplicamos a penalidade de descontentamento.
                ship.setGold(ship.getGold() + goldReward);
                eventLog.add(String.format("Você recebeu %d de ouro.", goldReward));
                return;
            }
        }

        // Estágio 1: Descontentamento (moral < 50)
        if (averageMorale < DISCONTENTMENT_THRESHOLD) {
            if (goldReward > 0 && ThreadLocalRandom.current().nextDouble() < DISCONTENTMENT_CHANCE) {
                int penalty = (int) (goldReward * GOLD_PENALTY_PERCENTAGE);
                int finalGold = goldReward - penalty;
                ship.setGold(ship.getGold() + finalGold);
                eventLog.add(String.format(
                    "MORAL BAIXA: Tripulantes descontentes causaram problemas! Um pequeno 'acidente' resultou na perda de %d de ouro.",
                    penalty
                ));
                eventLog.add(String.format("Você recebeu %d de ouro.", finalGold));
                return;
            }
        }

        // Se nenhuma penalidade ocorrer
        ship.setGold(ship.getGold() + goldReward);
        if (goldReward > 0) {
            eventLog.add(String.format("Você recebeu %d de ouro.", goldReward));
        }
    }

    /**
     * Aplica uma penalidade de perda de recursos aleatória devido à insubordinação.
     * @param ship O navio do jogador.
     * @param eventLog O log de eventos para adicionar a mensagem narrativa.
     */
    private void applyInsubordinationPenalty(Ship ship, List<String> eventLog) {
        int resourceType = ThreadLocalRandom.current().nextInt(3); // 0: Food, 1: Rum, 2: Repair Parts

        switch (resourceType) {
            case 0 -> {
                int foodLoss = (int) (ship.getFoodRations() * RESOURCE_LOSS_PERCENTAGE);
                if (foodLoss > 0) {
                    ship.setFoodRations(ship.getFoodRations() - foodLoss);
                    eventLog.add(String.format("INSUBORDINAÇÃO: A negligência tomou conta! %d porções de comida estragaram.", foodLoss));
                }
            }
            case 1 -> {
                int rumLoss = (int) (ship.getRumRations() * RESOURCE_LOSS_PERCENTAGE);
                if (rumLoss > 0) {
                    ship.setRumRations(ship.getRumRations() - rumLoss);
                    eventLog.add(String.format("INSUBORDINAÇÃO: Uma briga pelo rum resultou na perda de %d unidades.", rumLoss));
                }
            }
            case 2 -> {
                int partsLoss = (int) (ship.getRepairParts() * RESOURCE_LOSS_PERCENTAGE);
                if (partsLoss > 0) {
                    ship.setRepairParts(ship.getRepairParts() - partsLoss);
                    eventLog.add(String.format("INSUBORDINAÇÃO: Ferramentas foram 'perdidas' no mar. %d peças de reparo a menos.", partsLoss));
                }
            }
        }
    }

    /**
     * Processa o consumo de recursos e o pagamento de salários no final de um ciclo.
     * Aplica penalidades de moral se os recursos forem insuficientes.
     * @param ship O navio do jogador.
     * @param eventLog O log de eventos para adicionar mensagens narrativas.
     */
    private void endTurnCycle(Ship ship, List<String> eventLog) {
        List<CrewMember> crew = ship.getCrew();
        if (crew.isEmpty()) {
            return; // Nenhum ciclo de turno se não houver tripulação
        }

        int crewSize = crew.size();
        int totalFoodConsumption = crewSize * FOOD_CONSUMPTION_PER_CREW;
        int totalRumConsumption = crewSize * RUM_CONSUMPTION_PER_CREW;
        int totalSalary = crew.stream().mapToInt(CrewMember::getSalary).sum();

        // 1. Consumo de Comida
        if (ship.getFoodRations() >= totalFoodConsumption) {
            ship.setFoodRations(ship.getFoodRations() - totalFoodConsumption);
            eventLog.add(String.format("A tripulação consumiu %d porções de comida.", totalFoodConsumption));
        } else {
            ship.setFoodRations(0);
            eventLog.add("FIM DO TURNO: As rações de comida acabaram! A fome se espalha pela tripulação.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_FOOD));
        }

        // 2. Consumo de Rum
        if (ship.getRumRations() >= totalRumConsumption) {
            ship.setRumRations(ship.getRumRations() - totalRumConsumption);
            eventLog.add(String.format("A tripulação consumiu %d rações de rum.", totalRumConsumption));
        } else {
            ship.setRumRations(0);
            eventLog.add("FIM DO TURNO: O rum acabou! O descontentamento é visível.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_RUM));
        }

        // 3. Pagamento de Salários
        if (ship.getGold() >= totalSalary) {
            ship.setGold(ship.getGold() - totalSalary);
            eventLog.add(String.format("Você pagou %d de ouro em salários.", totalSalary));
        } else {
            eventLog.add("FIM DO TURNO: Ouro insuficiente para pagar os salários! A tripulação está à beira de um motim.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_PAY));
        }

        // Garantir que a moral não saia dos limites (0-100)
        crew.forEach(member -> member.setMoral(Math.max(0, Math.min(100, member.getMoral()))));
    }
}