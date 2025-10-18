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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final EventOptionRepository eventOptionRepository;
    private final ContractRepository contractRepository;
    private final PortRepository portRepository;
    private final SeaEncounterRepository seaEncounterRepository;
    private final ShipUpgradeRepository shipUpgradeRepository;
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

    // Constantes do Estaleiro
    private static final int REPAIR_COST_PER_POINT = 10;

    
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
                .build();

        Game newGame = Game.builder()
                .ship(newShip)
                .currentPort(startingPort)
                .gold(500) // Ouro inicial para testes
                .build();

        newShip.setGame(newGame);
        return gameRepository.save(newGame);
    }

    
    public Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + id));
    }

    
    public PortDTO getCurrentPort(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O jogo não está atracado em nenhum porto.");
        }
        return gameMapper.toPortDTO(currentPort);
    }

    
    public SeaEncounter travelToPort(Long gameId, TravelRequestDTO request) {
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

        endTurnCycle(game, eventLog);

        Game savedGame = gameRepository.save(game);

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
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
        contract.setStatus(ContractStatus.IN_PROGRESS);

        contractRepository.save(contract);
        return gameRepository.save(game);
    }

    
    public GameActionResponseDTO resolveContract(Long gameId) {
        Game game = findGameById(gameId);
        Contract activeContract = game.getActiveContract();
        List<String> eventLog = new ArrayList<>();

        if (activeContract == null) {
            throw new IllegalStateException("O jogo não possui um contrato ativo para resolver.");
        }

        eventLog.add("Contrato concluído: " + activeContract.getTitle());

        handleMoraleConsequences(game, activeContract.getRewardGold(), eventLog);

        game.setReputation(game.getReputation() + activeContract.getRewardReputation());
        game.setInfamy(game.getInfamy() + activeContract.getRewardInfamy());
        game.setAlliance(game.getAlliance() + activeContract.getRewardAlliance());

        endTurnCycle(game, eventLog);

        activeContract.setStatus(ContractStatus.COMPLETED);
        game.setActiveContract(null);

        contractRepository.save(activeContract);
        Game savedGame = gameRepository.save(game);

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
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

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
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

        return GameActionResponseDTO.builder()
            .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
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

            if (ship.getHullIntegrity() <= 0) {
                eventLog.add("DERROTA! Seu navio foi destruído. As ondas consomem seus destroços e seus sonhos...");
            }
        }

        Game savedGame = gameRepository.save(game);
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
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
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                .eventLog(eventLog)
                .build();
    }

    
    @Transactional(readOnly = true)
    public ShipyardDTO getShipyardInfo(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        if (game.getCurrentPort() == null) {
            throw new IllegalStateException("O navio deve estar em um porto para acessar o estaleiro.");
        }

        int damage = ship.getMaxHullIntegrity() - ship.getHullIntegrity();
        int repairCost = (damage > 0) ? damage * REPAIR_COST_PER_POINT : 0;

        List<ShipUpgrade> allUpgrades = shipUpgradeRepository.findAll();
        List<ShipUpgrade> currentUpgrades = ship.getUpgrades();

        List<ShipUpgradeDTO> availableUpgrades = allUpgrades.stream()
                .filter(upgrade -> !currentUpgrades.contains(upgrade))
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
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
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
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                .eventLog(eventLog)
                .build();
    }
}