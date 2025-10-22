package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.*;
import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.model.enums.IntroChoice;
import com.tidebreakerstudios.freedom_tide.repository.*;
import com.tidebreakerstudios.freedom_tide.service.tutorial.TutorialMetricsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final EventOptionRepository eventOptionRepository;
    private final ContractRepository contractRepository;
    private final PortRepository portRepository;
    private final SeaEncounterRepository seaEncounterRepository;
    private final ShipRepository shipRepository;
    private final ShipUpgradeRepository shipUpgradeRepository;
    private final GameMapper gameMapper;
    private final ContractService contractService;
    private final TutorialMetricsService tutorialMetricsService;

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

    // Constantes do Estaleiro
    private static final int REPAIR_COST_PER_POINT = 10;

    
    @Transactional
    public Game createNewGame() {
        Port startingPort = portRepository.findByName("Porto Real")
                .orElseThrow(() -> new IllegalStateException("Porto inicial 'Porto Real' não encontrado. O DataSeeder falhou?"));

        ShipType initialShipType = ShipType.SCHOONER;
        Ship newShip = Ship.builder()
                .name("O Andarilho")
                .type(initialShipType)
                .cannons(8)
                .crew(new ArrayList<>())
                .maxHullIntegrity(initialShipType.getMaxHull())
                .hullIntegrity(initialShipType.getMaxHull() - 20) // Navio começa com 20 de dano
                .foodRations(30) // Começa com poucos suprimentos para o tutorial
                .rumRations(15)  // Começa com poucos suprimentos para o tutorial
                .build();

        Game newGame = Game.builder()
                .ship(newShip)
                .currentPort(startingPort)
                .gold(1000) // Ouro inicial suficiente para o tutorial
                .build();

        newShip.setGame(newGame);
        return gameRepository.save(newGame);
    }

    
    public Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public GameStatusResponseDTO getGameStatusDTO(Long id) {
        Game game = findGameById(id);
        List<PortActionDTO> portActions = getAvailablePortActions(id);
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(id);
        return gameMapper.toGameStatusResponseDTO(game, portActions, encounterActions);
    }

    
    public PortDTO getCurrentPort(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O jogo não está atracado em nenhum porto.");
        }
        return gameMapper.toPortDTO(currentPort);
    }

    @Transactional(readOnly = true)
    public List<PortSummaryDTO> getTravelDestinations(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O jogador não está em um porto para ver os destinos de viagem.");
        }

        return portRepository.findAll().stream()
                .filter(port -> !port.equals(currentPort))
                .map(port -> new PortSummaryDTO(port.getId(), port.getName()))
                .collect(Collectors.toList());
    }

    
    public GameActionResponseDTO travelToPort(Long gameId, TravelRequestDTO request) {
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

        // O SeaEncounter é salvo em cascata a partir do Game
        Game savedGame = gameRepository.save(game);

        String departureMessage = String.format("Você zarpa de %s em direção a %s.", currentPort.getName(), destinationPort.getName());
        String encounterMessage = String.format("Encontro no mar: %s", encounter.getDescription());

        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(List.of(departureMessage, encounterMessage))
                .build();
    }

    private SeaEncounter generateRandomEncounter() {
        SeaEncounterType[] encounterTypes = SeaEncounterType.values();
        SeaEncounterType randomType = encounterTypes[new Random().nextInt(encounterTypes.length)];

        SeaEncounter.SeaEncounterBuilder encounterBuilder = SeaEncounter.builder().type(randomType);

        switch (randomType) {
            case MERCHANT_SHIP -> {
                encounterBuilder
                    .description("No horizonte, você avista as velas de um navio mercante solitário, navegando lentamente com sua carga.")
                    .hull(50)
                    .cannons(4)
                    .sails(6);
            }
            case PIRATE_VESSEL -> {
                encounterBuilder
                    .description("Um navio de velas negras e uma bandeira ameaçadora surge rapidamente, em rota de interceptação.")
                    .hull(80)
                    .cannons(8)
                    .sails(8);
            }
            case NAVY_PATROL -> {
                encounterBuilder
                    .description("Uma patrulha da Marinha Imperial, com seus canhões polidos e disciplina rígida, cruza o seu caminho.")
                    .hull(120)
                    .cannons(12)
                    .sails(7);
            }
            case MYSTERIOUS_WRECK -> {
                encounterBuilder
                    .description("Os destroços de um naufrágio aparecem à deriva, mastros quebrados apontando para o céu como dedos esqueléticos.")
                    .hull(0)
                    .cannons(0)
                    .sails(0);
            }
        }

        return encounterBuilder.build();
    }

    
    public List<PortActionDTO> getAvailablePortActions(Long gameId) {
        Game game = findGameById(gameId);
        if (game.getCurrentPort() == null) {
            return List.of();
        }

        List<PortActionDTO> actions = new ArrayList<>();

        actions.add(new PortActionDTO(
                PortActionType.VIEW_CONTRACTS,
                "Ver Contratos Disponíveis",
                "Veja os trabalhos e missões oferecidos neste porto.",
                "/api/games/" + gameId + "/contracts"
        ));

        actions.add(new PortActionDTO(
                PortActionType.TRAVEL,
                "Viajar",
                "Abra o mapa e escolha um destino para zarpar.",
                "/api/games/" + gameId + "/travel"
        ));

        actions.add(new PortActionDTO(
                PortActionType.GO_TO_SHIPYARD,
                "Ir ao Estaleiro",
                "Repare seu navio ou compre melhorias.",
                "/api/games/" + gameId + "/port/shipyard"
        ));

        actions.add(new PortActionDTO(
                PortActionType.GO_TO_MARKET,
                "Ir ao Mercado",
                "Compre e venda recursos como comida, rum e ferramentas.",
                "/api/games/" + gameId + "/port/market"
        ));

        actions.add(new PortActionDTO(
                PortActionType.GO_TO_TAVERN,
                "Ir à Taverna",
                "Procure por novos tripulantes para contratar.",
                "/api/games/" + gameId + "/port/tavern"
        ));

        return actions;
    }

    
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
                actions.add(new EncounterActionDTO(EncounterActionType.BOARD, "Abordar", "Tentar uma abordagem para capturar o navio.", basePath + "board"));
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

    
    public Game recruitCrewMember(Long gameId, RecruitCrewMemberRequest request) {
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
        return gameRepository.save(game);
    }

    
    public GameActionResponseDTO resolveEvent(Long gameId, ResolveEventRequest request) {
        Game game = findGameById(gameId);
        List<String> eventLog = new ArrayList<>();

        EventOption chosenOption = eventOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Opção de evento não encontrada com o ID: " + request.getOptionId()));

        eventLog.add("Evento resolvido: " + chosenOption.getNarrativeEvent().getTitle());
        EventConsequence consequence = chosenOption.getConsequence();

        game.setReputation(game.getReputation() + consequence.getReputationChange());
        game.setInfamy(game.getInfamy() + consequence.getInfamyChange());
        game.setAlliance(game.getAlliance() + consequence.getAllianceChange());
        game.getShip().setFoodRations(game.getShip().getFoodRations() + consequence.getFoodChange());
        game.getShip().setRumRations(game.getShip().getRumRations() + consequence.getRumChange());

        handleMoraleConsequences(game, consequence.getGoldChange(), eventLog);

        if (consequence.getCrewMoralChange() != 0) {
            int baseMoralChange = consequence.getCrewMoralChange();
            for (CrewMember member : game.getShip().getCrew()) {
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

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    
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
        contract.setGame(game); // Set the other side of the relationship
        contract.setStatus(ContractStatus.IN_PROGRESS);

        contractRepository.save(contract);
        return gameRepository.save(game);
    }

    
    @Transactional
    public GameActionResponseDTO resolveContract(Long gameId) {
        Game game = findGameById(gameId);
        List<String> eventLog = new ArrayList<>();

        // 1. Valida se as condições do contrato foram atendidas
        contractService.validateContractResolution(game);

        // 2. Procede com a resolução se a validação passar
        Contract activeContract = game.getActiveContract(); // Sabemos que não é nulo por causa da validação
        eventLog.add("Contrato concluído: " + activeContract.getTitle());

        handleMoraleConsequences(game, activeContract.getRewardGold(), eventLog);

        game.setReputation(game.getReputation() + activeContract.getRewardReputation());
        game.setInfamy(game.getInfamy() + activeContract.getRewardInfamy());
        game.setAlliance(game.getAlliance() + activeContract.getRewardAlliance());

        endTurnCycle(game, eventLog);

        activeContract.setStatus(ContractStatus.COMPLETED);
        game.setActiveContract(null);

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    private void handleMoraleConsequences(Game game, int goldReward, List<String> eventLog) {
        Ship ship = game.getShip();
        double averageMorale = ship.getAverageMorale();

        if (averageMorale < INSUBORDINATION_THRESHOLD) {
            if (ThreadLocalRandom.current().nextDouble() < INSUBORDINATION_CHANCE) {
                applyInsubordinationPenalty(ship, eventLog);
                game.setGold(game.getGold() + goldReward);
                eventLog.add(String.format("Você recebeu %d de ouro.", goldReward));
                return;
            }
        }

        if (averageMorale < DISCONTENTMENT_THRESHOLD) {
            if (goldReward > 0 && ThreadLocalRandom.current().nextDouble() < DISCONTENTMENT_CHANCE) {
                int penalty = (int) (goldReward * GOLD_PENALTY_PERCENTAGE);
                int finalGold = goldReward - penalty;
                game.setGold(game.getGold() + finalGold);
                eventLog.add(String.format(
                    "MORAL BAIXA: Tripulantes descontentes causaram problemas! Um pequeno 'acidente' resultou na perda de %d de ouro.",
                    penalty
                ));
                eventLog.add(String.format("Você recebeu %d de ouro.", finalGold));
                return;
            }
        }

        game.setGold(game.getGold() + goldReward);
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

    private void endTurnCycle(Game game, List<String> eventLog) {
        Ship ship = game.getShip();
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

        if (game.getGold() >= totalSalary) {
            game.setGold(game.getGold() - totalSalary);
            eventLog.add(String.format("Você pagou %d de ouro em salários.", totalSalary));
        } else {
            eventLog.add("FIM DO TURNO: Ouro insuficiente para pagar os salários! A tripulação está à beira de um motim.");
            crew.forEach(member -> member.setMoral(member.getMoral() + MORALE_PENALTY_NO_PAY));
        }

        crew.forEach(member -> member.setMoral(Math.max(0, Math.min(100, member.getMoral()))));
    }

    
    public GameActionResponseDTO fleeEncounter(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();

        if (game.getCurrentEncounter() == null || game.getDestinationPort() == null) {
            throw new IllegalStateException("Não há um encontro em andamento ou um destino definido para o qual fugir.");
        }

        int crewNavigationSkill = ship.getCrew().stream().mapToInt(CrewMember::getNavigation).sum();
        int escapeDifficulty = 10;
        int randomFactor = ThreadLocalRandom.current().nextInt(1, 21);
        boolean success = crewNavigationSkill > (escapeDifficulty + randomFactor);

        if (success) {
            Port destination = game.getDestinationPort();
            game.setCurrentPort(destination);
            game.setCurrentEncounter(null);
            game.setDestinationPort(null);

            eventLog.add(String.format("Com uma tripulação de Navegação %d, você superou a dificuldade (%d) e escapou!", crewNavigationSkill, escapeDifficulty + randomFactor));
            eventLog.add("Você chegou ao seu destino: " + destination.getName());

            endTurnCycle(game, eventLog);
        } else {
            int hullDamage = 5;
            ship.setHullIntegrity(ship.getHullIntegrity() - hullDamage);
            eventLog.add(String.format("Sua tripulação (Navegação %d) não foi páreo para a dificuldade (%d)!", crewNavigationSkill, escapeDifficulty + randomFactor));
            eventLog.add(String.format("Na tentativa de fuga desesperada, o navio sofreu %d de dano ao casco.", hullDamage));
        }

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    
    public GameActionResponseDTO investigateEncounter(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();

        SeaEncounter encounter = game.getCurrentEncounter();
        if (encounter == null || encounter.getType() != SeaEncounterType.MYSTERIOUS_WRECK) {
            throw new IllegalStateException("A ação 'Investigar' só pode ser usada em destroços misteriosos.");
        }

        Port destination = game.getDestinationPort();
        if (destination == null) {
            throw new IllegalStateException("Não há um destino definido para o qual viajar após a investigação.");
        }

        eventLog.add("Você ordena que a tripulação investigue os destroços...");

        if (ThreadLocalRandom.current().nextDouble() < 0.15) {
            int hullDamage = ThreadLocalRandom.current().nextInt(3, 8);
            ship.setHullIntegrity(ship.getHullIntegrity() - hullDamage);
            eventLog.add(String.format("RISCO: Ao se aproximar, destroços ocultos arranham o casco, causando %d de dano!", hullDamage));
        }

        int crewIntelligence = ship.getCrew().stream().mapToInt(CrewMember::getIntelligence).sum();
        int difficulty = 15;
        int randomFactor = ThreadLocalRandom.current().nextInt(1, 21);
        boolean success = crewIntelligence + randomFactor > difficulty;

        if (success) {
            int goldFound = ThreadLocalRandom.current().nextInt(100, 251);
            int partsFound = ThreadLocalRandom.current().nextInt(5, 11);
            game.setGold(game.getGold() + goldFound);
            ship.setRepairParts(ship.getRepairParts() + partsFound);
            eventLog.add(String.format(
                "SUCESSO: A tripulação (Inteligência %d + rolagem %d) superou a dificuldade (%d)! Eles encontram um compartimento secreto contendo %d de ouro e %d peças de reparo.",
                crewIntelligence, randomFactor, difficulty, goldFound, partsFound
            ));
        } else {
            int goldFound = ThreadLocalRandom.current().nextInt(20, 51);
            game.setGold(game.getGold() + goldFound);
            eventLog.add(String.format(
                "FALHA: A tripulação (Inteligência %d + rolagem %d) não superou a dificuldade (%d). Após muita busca, encontram apenas %d de ouro nos bolsos de um esqueleto.",
                crewIntelligence, randomFactor, difficulty, goldFound
            ));
        }

        game.setCurrentPort(destination);
        game.setCurrentEncounter(null);
        game.setDestinationPort(null);
        eventLog.add("Com os destroços vasculhados, você continua sua viagem e chega a " + destination.getName() + ".");

        endTurnCycle(game, eventLog);

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());

        return GameActionResponseDTO.builder()
            .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
            .eventLog(eventLog)
            .build();
    }

    
    public GameActionResponseDTO attackEncounter(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        SeaEncounter encounter = game.getCurrentEncounter();
        List<String> eventLog = new ArrayList<>();

        if (encounter == null || encounter.getType() == SeaEncounterType.MYSTERIOUS_WRECK) {
            throw new IllegalStateException("Não há um alvo válido para atacar.");
        }

        int playerArtillery = ship.getCrew().stream().mapToInt(CrewMember::getArtillery).sum();
        int playerDamage = playerArtillery + ThreadLocalRandom.current().nextInt(5, 11);
        encounter.setHull(encounter.getHull() - playerDamage);
        eventLog.add(String.format("Você ordena o ataque! Seus artilheiros (Habilidade %d) disparam uma salva de canhões, causando %d de dano ao casco inimigo.", playerArtillery, playerDamage));

        if (encounter.getHull() <= 0) {
            eventLog.add(String.format("VITÓRIA! O navio inimigo, %s, está em destroços!", encounter.getType()));

            int goldReward = 0;
            switch (encounter.getType()) {
                case MERCHANT_SHIP -> {
                    goldReward = 200;
                    game.setInfamy(game.getInfamy() + 25);
                    eventLog.add(String.format("Você saqueia os destroços e encontra %d de ouro. Sua infâmia aumenta.", goldReward));
                }
                case PIRATE_VESSEL -> {
                    goldReward = 100;
                    game.setReputation(game.getReputation() + 15);
                    eventLog.add(String.format("Você recupera %d de ouro dos piratas. O Império vê sua ação com bons olhos.", goldReward));
                }
                case NAVY_PATROL -> {
                    goldReward = 50;
                    game.setAlliance(game.getAlliance() + 30);
                    eventLog.add(String.format("Apesar de encontrar apenas %d de ouro, derrotar a patrulha inspira os oprimidos. Sua Aliança cresce.", goldReward));
                }
            }
            game.setGold(game.getGold() + goldReward);

            Port destination = game.getDestinationPort();
            game.setCurrentPort(destination);
            game.setCurrentEncounter(null);
            game.setDestinationPort(null);
            eventLog.add("Com a batalha terminada, você continua sua viagem e chega a " + destination.getName() + ".");
            endTurnCycle(game, eventLog);

        } else {
            int enemyDamage = encounter.getCannons() + ThreadLocalRandom.current().nextInt(1, 7);
            ship.setHullIntegrity(ship.getHullIntegrity() - enemyDamage);
            eventLog.add(String.format("O inimigo revida! Os canhões deles causam %d de dano ao seu casco.", enemyDamage));
        }

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    
    public GameActionResponseDTO boardEncounter(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        SeaEncounter encounter = game.getCurrentEncounter();
        List<String> eventLog = new ArrayList<>();

        if (encounter == null || encounter.getType() == SeaEncounterType.MYSTERIOUS_WRECK) {
            throw new IllegalStateException("Não há um alvo válido para abordar.");
        }

        eventLog.add("Você dá a ordem e sua tripulação se prepara para a abordagem!");

        int playerCombatStrength = ship.getCrew().stream().mapToInt(CrewMember::getCombat).sum();
        int enemyBaseStrength = switch (encounter.getType()) {
            case MERCHANT_SHIP -> 10;
            case PIRATE_VESSEL -> 20;
            case NAVY_PATROL -> 30;
            default -> 0;
        };

        int playerRoll = playerCombatStrength + ThreadLocalRandom.current().nextInt(1, 21);
        int enemyRoll = enemyBaseStrength + ThreadLocalRandom.current().nextInt(1, 21);

        eventLog.add(String.format("Sua força de abordagem (Combate %d + rolagem) resultou em %d.", playerCombatStrength, playerRoll));
        eventLog.add(String.format("A força de defesa inimiga (Base %d + rolagem) resultou em %d.", enemyBaseStrength, enemyRoll));

        if (playerRoll > enemyRoll) {
            eventLog.add("VITÓRIA! Sua tripulação domina o convés inimigo e força a rendição!");

            int goldReward = 0;
            switch (encounter.getType()) {
                case MERCHANT_SHIP -> {
                    goldReward = 400;
                    game.setInfamy(game.getInfamy() + 30);
                    eventLog.add(String.format("Você captura o navio e sua carga, obtendo %d de ouro. Sua infâmia cresce.", goldReward));
                }
                case PIRATE_VESSEL -> {
                    goldReward = 200;
                    game.setReputation(game.getReputation() + 20);
                    eventLog.add(String.format("Você captura os piratas, recuperando %d de ouro. O Império agradece.", goldReward));
                }
                case NAVY_PATROL -> {
                    goldReward = 100;
                    game.setAlliance(game.getAlliance() + 40);
                    eventLog.add(String.format("Capturar um navio da Marinha é um ato ousado! Você encontra %d de ouro e sua Aliança com os rebeldes se fortalece.", goldReward));
                }
            }
            game.setGold(game.getGold() + goldReward);

            Port destination = game.getDestinationPort();
            game.setCurrentPort(destination);
            game.setCurrentEncounter(null);
            game.setDestinationPort(null);
            eventLog.add("Com o navio inimigo capturado, você continua sua viagem e chega a " + destination.getName() + ".");
            endTurnCycle(game, eventLog);

        } else {
            int hullDamage = ThreadLocalRandom.current().nextInt(10, 21);
            int moralePenalty = -15;
            ship.setHullIntegrity(ship.getHullIntegrity() - hullDamage);
            ship.getCrew().forEach(member -> member.setMoral(member.getMoral() + moralePenalty));

            eventLog.add(String.format("DERROTA! Sua tripulação foi repelida! Em meio à retirada caótica, seu navio sofre %d de dano ao casco e a moral da tripulação despenca.", hullDamage));
        }

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    
    @Transactional(readOnly = true)
    public ShipyardDTO getShipyardInfo(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        Port currentPort = game.getCurrentPort();

        if (currentPort == null) {
            throw new IllegalStateException("O navio deve estar em um porto para acessar o estaleiro.");
        }

        int damage = ship.getMaxHullIntegrity() - ship.getHullIntegrity();
        int repairCost = (damage > 0) ? damage * REPAIR_COST_PER_POINT : 0;

        List<ShipUpgrade> allUpgrades = shipUpgradeRepository.findAll();
        List<ShipUpgrade> currentUpgrades = ship.getUpgrades();
        PortType portType = currentPort.getType();

        List<ShipUpgradeDTO> availableUpgrades = allUpgrades.stream()
                .filter(upgrade -> !currentUpgrades.contains(upgrade)) // Filtra o que o jogador já tem
                .filter(upgrade -> upgrade.getPortType() == null || upgrade.getPortType() == portType) // Filtra por facção do porto
                .map(gameMapper::toShipUpgradeDTO)
                .collect(Collectors.toList());

        String message = (damage > 0)
                ? String.format("O mestre do estaleiro estima o custo para reparar %d pontos de dano no casco.", damage)
                : "O estaleiro não tem trabalho a fazer. Seu navio está em perfeitas condições.";

        return ShipyardDTO.builder()
                .message(message)
                .repairCost(repairCost)
                .hullIntegrity(ship.getHullIntegrity())
                .maxHullIntegrity(ship.getMaxHullIntegrity())
                .availableUpgrades(availableUpgrades)
                .build();
    }

    
    public GameActionResponseDTO repairShip(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();

        if (game.getCurrentPort() == null) {
            throw new IllegalStateException("O navio deve estar em um porto para reparos.");
        }

        int damage = ship.getMaxHullIntegrity() - ship.getHullIntegrity();
        if (damage <= 0) {
            eventLog.add("Seu navio já está com o casco em estado máximo. Nenhum reparo foi necessário.");
            return GameActionResponseDTO.builder()
                    .gameStatus(gameMapper.toGameStatusResponseDTO(game))
                    .eventLog(eventLog)
                    .build();
        }

        int repairCost = damage * REPAIR_COST_PER_POINT;
        if (game.getGold() < repairCost) {
            throw new IllegalStateException(String.format("Ouro insuficiente. Você precisa de %d de ouro para os reparos, mas possui apenas %d.", repairCost, game.getGold()));
        }

        game.setGold(game.getGold() - repairCost);
        ship.setHullIntegrity(ship.getMaxHullIntegrity());

        eventLog.add(String.format("Você pagou %d de ouro ao estaleiro. O casco do seu navio foi totalmente reparado!", repairCost));

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    @Transactional
    public GameActionResponseDTO purchaseUpgrade(Long gameId, Long upgradeId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();

        if (game.getCurrentPort() == null) {
            throw new IllegalStateException("O navio deve estar em um porto para comprar melhorias.");
        }

        ShipUpgrade upgradeToPurchase = shipUpgradeRepository.findById(upgradeId)
                .orElseThrow(() -> new EntityNotFoundException("Melhoria não encontrada com o ID: " + upgradeId));

        if (ship.getUpgrades().contains(upgradeToPurchase)) {
            throw new IllegalStateException("Seu navio já possui a melhoria: " + upgradeToPurchase.getName());
        }

        if (game.getGold() < upgradeToPurchase.getCost()) {
            throw new IllegalStateException(String.format("Ouro insuficiente. Você precisa de %d de ouro para comprar '%s', mas possui apenas %d.",
                    upgradeToPurchase.getCost(), upgradeToPurchase.getName(), game.getGold()));
        }

        // Apply purchase
        game.setGold(game.getGold() - upgradeToPurchase.getCost());
        ship.getUpgrades().add(upgradeToPurchase);

        // Apply modifier
        switch (upgradeToPurchase.getType()) {
            case HULL:
                ship.setMaxHullIntegrity(ship.getMaxHullIntegrity() + upgradeToPurchase.getModifier());
                ship.setHullIntegrity(ship.getHullIntegrity() + upgradeToPurchase.getModifier()); // Also repair to the new max
                break;
            case CANNONS:
                ship.setCannons(ship.getCannons() + upgradeToPurchase.getModifier());
                break;
            // Other cases for SAILS, CARGO etc. can be added here
        }

        eventLog.add(String.format("Você pagou %d de ouro e instalou a melhoria: %s!",
                upgradeToPurchase.getCost(), upgradeToPurchase.getName()));

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    @Transactional(readOnly = true)
    public MarketDTO getMarketInfo(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O navio deve estar em um porto para acessar o mercado.");
        }

        Ship ship = game.getShip();
        return MarketDTO.builder()
                .foodPrice(currentPort.getFoodPrice())
                .rumPrice(currentPort.getRumPrice())
                .toolsPrice(currentPort.getToolsPrice())
                .shotPrice(currentPort.getShotPrice())
                .shipFood(ship.getFoodRations())
                .shipRum(ship.getRumRations())
                .shipTools(ship.getRepairParts())
                .shipShot(ship.getShot())
                .shipGold(game.getGold())
                .build();
    }

    @Transactional
    public GameActionResponseDTO buyMarketItem(Long gameId, MarketTransactionRequest request) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O navio deve estar em um porto para comprar itens.");
        }

        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();
        int pricePerUnit = 0;

        switch (request.getItem()) {
            case FOOD -> pricePerUnit = currentPort.getFoodPrice();
            case RUM -> pricePerUnit = currentPort.getRumPrice();
            case TOOLS -> pricePerUnit = currentPort.getToolsPrice();
            case SHOT -> pricePerUnit = currentPort.getShotPrice();
        }

        int totalCost = pricePerUnit * request.getQuantity();
        if (game.getGold() < totalCost) {
            throw new IllegalStateException(String.format("Ouro insuficiente. Você precisa de %d, mas tem apenas %d.", totalCost, game.getGold()));
        }

        game.setGold(game.getGold() - totalCost);

        switch (request.getItem()) {
            case FOOD -> ship.setFoodRations(ship.getFoodRations() + request.getQuantity());
            case RUM -> ship.setRumRations(ship.getRumRations() + request.getQuantity());
            case TOOLS -> ship.setRepairParts(ship.getRepairParts() + request.getQuantity());
            case SHOT -> ship.setShot(ship.getShot() + request.getQuantity());
        }

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    @Transactional
    public GameActionResponseDTO sellMarketItem(Long gameId, MarketTransactionRequest request) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O navio deve estar em um porto para vender itens.");
        }

        Ship ship = game.getShip();
        List<String> eventLog = new ArrayList<>();
        int pricePerUnit = 0;

        // Para simplificar, o preço de venda é o mesmo de compra. Poderíamos adicionar um spread depois.
        switch (request.getItem()) {
            case FOOD -> pricePerUnit = currentPort.getFoodPrice();
            case RUM -> pricePerUnit = currentPort.getRumPrice();
            case TOOLS -> pricePerUnit = currentPort.getToolsPrice();
            case SHOT -> pricePerUnit = currentPort.getShotPrice();
        }

        int currentQuantity = 0;
        switch (request.getItem()) {
            case FOOD -> currentQuantity = ship.getFoodRations();
            case RUM -> currentQuantity = ship.getRumRations();
            case TOOLS -> currentQuantity = ship.getRepairParts();
            case SHOT -> currentQuantity = ship.getShot();
        }

        if (currentQuantity < request.getQuantity()) {
            throw new IllegalStateException(String.format("Recursos insuficientes. Você tentou vender %d de %s, mas tem apenas %d.", request.getQuantity(), request.getItem(), currentQuantity));
        }

        int totalGain = pricePerUnit * request.getQuantity();
        game.setGold(game.getGold() + totalGain);

        switch (request.getItem()) {
            case FOOD -> ship.setFoodRations(ship.getFoodRations() - request.getQuantity());
            case RUM -> ship.setRumRations(ship.getRumRations() - request.getQuantity());
            case TOOLS -> ship.setRepairParts(ship.getRepairParts() - request.getQuantity());
            case SHOT -> ship.setShot(ship.getShot() - request.getQuantity());
        }

        eventLog.add(String.format("Você vendeu %d unidades de %s por %d de ouro.", request.getQuantity(), request.getItem(), totalGain));

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TavernRecruitDTO> getTavernRecruits(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O navio deve estar em um porto para visitar a taverna.");
        }

        List<TavernRecruitDTO> recruits = new ArrayList<>();
        int numberOfRecruits = ThreadLocalRandom.current().nextInt(2, 4); // Gera de 2 a 3 recrutas

        for (int i = 0; i < numberOfRecruits; i++) {
            String name = generateRandomName();
            CrewPersonality personality = generateRandomPersonality(currentPort.getType());
            int despair = ThreadLocalRandom.current().nextInt(1, 11);

            int nav = ThreadLocalRandom.current().nextInt(1, 6);
            int art = ThreadLocalRandom.current().nextInt(1, 6);
            int com = ThreadLocalRandom.current().nextInt(1, 6);
            int med = ThreadLocalRandom.current().nextInt(1, 6);
            int car = ThreadLocalRandom.current().nextInt(1, 6);
            int intel = ThreadLocalRandom.current().nextInt(1, 6);

            int attributeSum = nav + art + com + med + car + intel;
            int salary = Math.max(1, 10 + (attributeSum / 10) - despair);
            int initialMoral = Math.max(0, Math.min(100, 50 + (personality.getMoralModifier() * 5) - (despair * 2)));

            RecruitCrewMemberRequest request = new RecruitCrewMemberRequest();
            request.setName(name);
            request.setPersonality(personality);
            request.setDespairLevel(despair);
            request.setNavigation(nav);
            request.setArtillery(art);
            request.setCombat(com);
            request.setMedicine(med);
            request.setCarpentry(car);
            request.setIntelligence(intel);

            TavernRecruitDTO dto = TavernRecruitDTO.builder()
                    .name(name)
                    .personality(personality)
                    .despairLevel(despair)
                    .salary(salary)
                    .initialMoral(initialMoral)
                    .navigation(nav)
                    .artillery(art)
                    .combat(com)
                    .medicine(med)
                    .carpentry(car)
                    .intelligence(intel)
                    .recruitRequest(request)
                    .build();

            recruits.add(dto);
        }

        return recruits;
    }

    private String generateRandomName() {
        List<String> firstNames = List.of("Jack", "Anne", "William", "Mary", "Edward", "Eliza", "James", "Charlotte");
        List<String> lastNames = List.of("Rackham", "Bonny", "Kidd", "Read", "Teach", "Swan", "Morgan", "Blade");
        String firstName = firstNames.get(ThreadLocalRandom.current().nextInt(firstNames.size()));
        String lastName = lastNames.get(ThreadLocalRandom.current().nextInt(lastNames.size()));
        return firstName + " " + lastName;
    }

    private CrewPersonality generateRandomPersonality(PortType portType) {
        List<CrewPersonality> possibilities = new ArrayList<>(List.of(CrewPersonality.values()));
        // Adiciona peso baseado no tipo de porto
        switch (portType) {
            case IMPERIAL -> possibilities.add(CrewPersonality.HONEST); // Aumenta a chance de Honestos
            case GUILD -> possibilities.add(CrewPersonality.GREEDY);    // Aumenta a chance de Gananciosos
            case PIRATE -> {
                possibilities.add(CrewPersonality.BLOODTHIRSTY);
                possibilities.add(CrewPersonality.REBEL);
            } // Aumenta a chance de Sedentos por Sangue e Rebeldes
        }
        return possibilities.get(ThreadLocalRandom.current().nextInt(possibilities.size()));
    }

    /**
     * Processa a escolha inicial do jogador na sequência de introdução,
     * definindo os valores iniciais da Bússola do Capitão.
     */
    @Transactional
    public GameActionResponseDTO processIntroChoice(Long gameId, String choice) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com ID: " + gameId));
        
        List<String> eventMessages = new ArrayList<>();
        
        // Definir a escolha inicial para personalizar o tutorial
        IntroChoice introChoiceEnum;
        
        switch (choice.toLowerCase()) {
            case "cooperate" -> {
                introChoiceEnum = IntroChoice.COOPERATE;
                // Contrato da Guilda: +Reputação, mas cumplicidade
                game.setReputation(Math.min(1000, game.getReputation() + 50));
                game.setInfamy(Math.max(0, game.getInfamy() - 10));
                game.setAlliance(Math.max(0, game.getAlliance() - 20));
                eventMessages.add("Você aceita o contrato da Guilda. Sua reputação cresce, mas o peso da cumplicidade pesa em sua consciência.");
            }
            case "resist" -> {
                introChoiceEnum = IntroChoice.RESIST;
                // Saque violento: +Infâmia, violência
                game.setInfamy(Math.min(1000, game.getInfamy() + 60));
                game.setReputation(Math.max(0, game.getReputation() - 40));
                game.setAlliance(Math.max(0, game.getAlliance() - 30));
                eventMessages.add("Você escolhe a força bruta. Sua infâmia cresce, mas inocentes pagam o preço de sua sobrevivência.");
            }
            case "neutral" -> {
                introChoiceEnum = IntroChoice.NEUTRAL;
                // Contrabando humanitário: +Aliança, -Reputação, risco
                game.setAlliance(Math.min(1000, game.getAlliance() + 80));
                game.setReputation(Math.max(0, game.getReputation() - 30));
                game.setInfamy(Math.min(1000, game.getInfamy() + 20));
                eventMessages.add("Você arrisca tudo para ajudar os necessitados. Ganha aliados entre os oprimidos, mas se torna inimigo do sistema.");
            }
            default -> {
                introChoiceEnum = IntroChoice.COOPERATE; // Default fallback
                eventMessages.add("Escolha não reconhecida. Mantendo valores padrão.");
            }
        }
        
        // Definir a escolha inicial para personalizar o tutorial
        game.setIntroChoice(introChoiceEnum);
        
        gameRepository.save(game);
        
        // Iniciar rastreamento de métricas do tutorial
        tutorialMetricsService.startTutorialSession(gameId, introChoiceEnum);
        
        // Buscar ações disponíveis no porto atual
        List<PortActionDTO> portActions = getAvailablePortActions(gameId);
        
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(game, portActions))
                .eventLog(eventMessages)
                .build();
    }
}