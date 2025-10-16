package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.*;
import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final EventOptionRepository eventOptionRepository;
    private final ContractRepository contractRepository;
    private final PortRepository portRepository;
    private final SeaEncounterRepository seaEncounterRepository;
    private final GameMapper gameMapper;

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

    // ... (rest of the class methods from before)

    @Transactional
    public Game createNewGame() {
        Port startingPort = portRepository.findByName("Porto Real")
                .orElseThrow(() -> new IllegalStateException("Porto inicial 'Porto Real' não encontrado. O DataSeeder falhou?"));

        Ship newShip = Ship.builder().name("O Andarilho").type(ShipType.SCHOONER).crew(new ArrayList<>()).build();
        Game newGame = Game.builder().ship(newShip).currentPort(startingPort).build();
        newShip.setGame(newGame);
        return gameRepository.save(newGame);
    }

    @Transactional(readOnly = true)
    public Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public com.tidebreakerstudios.freedom_tide.dto.PortDTO getCurrentPort(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O jogo não está atracado em nenhum porto.");
        }
        return gameMapper.toPortDTO(currentPort);
    }

    @Transactional
    public SeaEncounter travelToPort(Long gameId, com.tidebreakerstudios.freedom_tide.dto.TravelRequestDTO request) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();

        if (currentPort == null) {
            throw new IllegalStateException("Não é possível viajar, o jogo já está em alto mar.");
        }
        if (game.getCurrentEncounter() != null) {
            throw new IllegalStateException("Não é possível viajar durante um encontro.");
        }

        Port destinationPort = portRepository.findById(request.destinationPortId())
                .orElseThrow(() -> new EntityNotFoundException("Porto de destino não encontrado com o ID: " + request.destinationPortId()));

        if (destinationPort.equals(currentPort)) {
            throw new IllegalStateException("O porto de destino não pode ser o mesmo que o porto atual.");
        }

        game.setCurrentPort(null);
        game.setDestinationPort(destinationPort);

        SeaEncounter encounter = generateRandomEncounter();
        game.setCurrentEncounter(encounter);

        seaEncounterRepository.save(encounter);
        gameRepository.save(game);

        return encounter;
    }

    private SeaEncounter generateRandomEncounter() {
        SeaEncounterType[] encounterTypes = SeaEncounterType.values();
        SeaEncounterType randomType = encounterTypes[new Random().nextInt(encounterTypes.length)];

        String description = switch (randomType) {
            case MERCHANT_SHIP -> "No horizonte, você avista as velas de um navio mercante solitário, navegando lentamente com sua carga.";
            case PIRATE_VESSEL -> "Um navio de velas negras e uma bandeira ameaçadora surge rapidamente, em rota de interceptação.";
            case NAVY_PATROL -> "Uma patrulha da Marinha Imperial, com seus canhões polidos e disciplina rígida, cruza o seu caminho.";
            case MYSTERIOUS_WRECK -> "Os destroços de um naufrágio aparecem à deriva, mastros quebrados apontando para o céu como dedos esqueléticos.";
        };

        return SeaEncounter.builder()
                .type(randomType)
                .description(description)
                .build();
    }

    @Transactional(readOnly = true)
    public List<com.tidebreakerstudios.freedom_tide.dto.PortActionDTO> getAvailablePortActions(Long gameId) {
        Game game = findGameById(gameId);
        if (game.getCurrentPort() == null) {
            return List.of();
        }

        List<com.tidebreakerstudios.freedom_tide.dto.PortActionDTO> actions = new ArrayList<>();

        actions.add(new com.tidebreakerstudios.freedom_tide.dto.PortActionDTO(
                PortActionType.VIEW_CONTRACTS,
                "Ver Contratos Disponíveis",
                "Veja os trabalhos e missões oferecidos neste porto.",
                "/api/games/" + gameId + "/contracts"
        ));

        actions.add(new com.tidebreakerstudios.freedom_tide.dto.PortActionDTO(
                PortActionType.TRAVEL,
                "Viajar",
                "Abra o mapa e escolha um destino para zarpar.",
                "/api/games/" + gameId + "/travel"
        ));

        return actions;
    }

    @Transactional(readOnly = true)
    public List<EncounterActionDTO> getAvailableEncounterActions(Long gameId) {
        Game game = findGameById(gameId);
        SeaEncounter encounter = game.getCurrentEncounter();

        if (encounter == null) {
            return List.of();
        }

        List<EncounterActionDTO> actions = new ArrayList<>();
        String basePath = "/api/games/" + gameId + "/encounter/";

        switch (encounter.getType()) {
            case MERCHANT_SHIP, PIRATE_VESSEL, NAVY_PATROL -> {
                actions.add(new EncounterActionDTO(EncounterActionType.ATTACK, "Atacar", "Iniciar combate naval.", basePath + "attack"));
                actions.add(new EncounterActionDTO(EncounterActionType.FLEE, "Fugir", "Tentar escapar do encontro.", basePath + "flee"));
            }
            case MYSTERIOUS_WRECK -> {
                actions.add(new EncounterActionDTO(EncounterActionType.INVESTIGATE, "Investigar", "Explorar os destroços em busca de recursos ou sobreviventes.", basePath + "investigate"));
            }
        }
        if (encounter.getType() == SeaEncounterType.MERCHANT_SHIP) {
            actions.add(new EncounterActionDTO(EncounterActionType.NEGOTIATE, "Negociar", "Tentar uma abordagem diplomática ou comercial.", basePath + "negotiate"));
        }

        return actions;
    }

    // ... (rest of the class methods)
    @Transactional
    public CrewMember recruitCrewMember(Long gameId, RecruitCrewMemberRequest request) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();

        CrewMember newCrewMember = CrewMember.builder()
                .name(request.getName()).personality(request.getPersonality()).despairLevel(request.getDespairLevel())
                .navigation(request.getNavigation()).artillery(request.getArtillery()).combat(request.getCombat())
                .medicine(request.getMedicine()).carpentry(request.getCarpentry()).intelligence(request.getIntelligence())
                .ship(ship).build();

        int baseMoral = 50;
        int personalityModifier = request.getPersonality().getMoralModifier() * 5;
        int despairPenalty = request.getDespairLevel() * 2;
        int initialMoral = baseMoral + personalityModifier - despairPenalty;
        newCrewMember.setMoral(Math.max(0, Math.min(100, initialMoral)));

        int attributeSum = request.getNavigation() + request.getArtillery() + request.getCombat() +
                           request.getMedicine() + request.getCarpentry() + request.getIntelligence();
        int baseSalary = 10;
        int salary = baseSalary + (attributeSum / 10) - request.getDespairLevel();
        newCrewMember.setSalary(Math.max(1, salary));

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

        game.setReputation(game.getReputation() + consequence.getReputationChange());
        game.setInfamy(game.getInfamy() + consequence.getInfamyChange());
        game.setAlliance(game.getAlliance() + consequence.getAllianceChange());
        ship.setFoodRations(ship.getFoodRations() + consequence.getFoodChange());
        ship.setRumRations(ship.getRumRations() + consequence.getRumChange());

        handleMoraleConsequences(ship, consequence.getGoldChange(), eventLog);

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

        handleMoraleConsequences(ship, activeContract.getRewardGold(), eventLog);

        game.setReputation(game.getReputation() + activeContract.getRewardReputation());
        game.setInfamy(game.getInfamy() + activeContract.getRewardInfamy());
        game.setAlliance(game.getAlliance() + activeContract.getRewardAlliance());

        endTurnCycle(ship, eventLog);

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

        if (averageMorale < INSUBORDINATION_THRESHOLD) {
            if (ThreadLocalRandom.current().nextDouble() < INSUBORDINATION_CHANCE) {
                applyInsubordinationPenalty(ship, eventLog);
                ship.setGold(ship.getGold() + goldReward);
                eventLog.add(String.format("Você recebeu %d de ouro.", goldReward));
                return;
            }
        }

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

        ship.setGold(ship.getGold() + goldReward);
        if (goldReward > 0) {
            eventLog.add(String.format("Você recebeu %d de ouro.", goldReward));
        }
    }

    private void applyInsubordinationPenalty(Ship ship, List<String> eventLog) {
        int resourceType = ThreadLocalRandom.current().nextInt(3);

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

    private void endTurnCycle(Ship ship, List<String> eventLog) {
        List<CrewMember> crew = ship.getCrew();
        if (crew.isEmpty()) {
            return;
        }

        int crewSize = crew.size();
        int totalFoodConsumption = crewSize * FOOD_CONSUMPTION_PER_CREW;
        int totalRumConsumption = crewSize * RUM_CONSUMPTION_PER_CREW;
        int totalSalary = crew.stream().mapToInt(CrewMember::getSalary).sum();

        if (ship.getFoodRations() >= totalFoodConsumption) {
            ship.setFoodRations(ship.getFoodRations() - totalFoodConsumption);
            eventLog.add(String.format("A tripulação consumiu %d porções de comida.", totalFoodConsumption));
        } else {
            ship.setFoodRations(0);
            eventLog.add("FIM DO TURNO: As rações de comida acabaram! A fome se espalha pela tripulação.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_FOOD));
        }

        if (ship.getRumRations() >= totalRumConsumption) {
            ship.setRumRations(ship.getRumRations() - totalRumConsumption);
            eventLog.add(String.format("A tripulação consumiu %d rações de rum.", totalRumConsumption));
        } else {
            ship.setRumRations(0);
            eventLog.add("FIM DO TURNO: O rum acabou! O descontentamento é visível.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_RUM));
        }

        if (ship.getGold() >= totalSalary) {
            ship.setGold(ship.getGold() - totalSalary);
            eventLog.add(String.format("Você pagou %d de ouro em salários.", totalSalary));
        } else {
            eventLog.add("FIM DO TURNO: Ouro insuficiente para pagar os salários! A tripulação está à beira de um motim.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_PAY));
        }

        crew.forEach(member -> member.setMoral(Math.max(0, Math.min(100, member.getMoral()))));
    }

    @Transactional
    public GameActionResponseDTO fleeEncounter(Long gameId) {
        Game game = findGameById(gameId);
        List<String> eventLog = new ArrayList<>();

        if (game.getCurrentEncounter() == null || game.getDestinationPort() == null) {
            throw new IllegalStateException("Não há um encontro em andamento ou um destino definido para o qual fugir.");
        }

        // MVP: A fuga sempre tem sucesso.
        // Futuramente, podemos adicionar uma lógica baseada em atributos (Navegação vs. do inimigo).
        Port destination = game.getDestinationPort();
        game.setCurrentPort(destination);
        game.setCurrentEncounter(null);
        game.setDestinationPort(null);

        eventLog.add("Você conseguiu escapar do encontro e navegar em segurança.");
        eventLog.add("Você chegou ao seu destino: " + destination.getName());

        // O ciclo de fim de turno (consumo/salários) pode ser acionado aqui para representar a passagem do tempo na viagem.
        endTurnCycle(game.getShip(), eventLog);

        Game savedGame = gameRepository.save(game);

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                .eventLog(eventLog)
                .build();
    }
}