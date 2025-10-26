package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.*;
import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.model.enums.IntroChoice;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewProfession;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewRank;
import com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase;
import com.tidebreakerstudios.freedom_tide.repository.*;
import com.tidebreakerstudios.freedom_tide.service.tutorial.TutorialMetricsService;
import com.tidebreakerstudios.freedom_tide.dto.tutorial.TutorialProgressRequestDTO;
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
    private final CrewProgressionService crewProgressionService;
    private final CaptainProgressionService captainProgressionService;
    private final UniqueCharacterService uniqueCharacterService;
    private final GameMapper gameMapper;
    private final ContractService contractService;
    private final TutorialMetricsService tutorialMetricsService;
    private final ContractEncounterService contractEncounterService;
    private final TutorialService tutorialService;

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
        return createNewGame("Capitão Anônimo");
    }

    @Transactional
    public Game createNewGame(String captainName) {
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
                .foodRations(30) // Suficiente para ~6 viagens (mais confortável)
                .rumRations(12)  // Suficiente para ~6 viagens (mais confortável)
                .build();

        Game newGame = Game.builder()
                .captainName(captainName)
                .ship(newShip)
                .currentPort(startingPort)
                .gold(1200) // Aumentado de 1000 para 1200 devido aos custos de viagem
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

        // Aplicar custos de viagem
        List<String> travelCosts = applyTravelCosts(game);

        SeaEncounter encounter = generateRandomEncounter(game);
        game.setCurrentEncounter(encounter);

        // O SeaEncounter é salvo em cascata a partir do Game
        Game savedGame = gameRepository.save(game);

        // Notificar tutorial sobre a viagem (se em tutorial)
        if (!savedGame.isTutorialCompleted() && savedGame.getTutorialPhase() == TutorialPhase.JOURNEY_START) {
            tutorialService.progressTutorial(savedGame.getId(), 
                TutorialProgressRequestDTO.builder().action("TRAVEL").build());
        }

        // Conceder XP de navegação para navegadores
        List<String> progressMessages = crewProgressionService.awardNavigationXP(savedGame);

        String departureMessage = String.format("Você zarpa de %s em direção a %s.", currentPort.getName(), destinationPort.getName());
        String encounterMessage = String.format("Encontro no mar: %s", encounter.getDescription());
        
        List<String> eventLog = new ArrayList<>();
        eventLog.add(departureMessage);
        
        // Adicionar explicação na primeira viagem
        if (game.getGold() >= 1180) { // Detecta se é próximo da primeira viagem
            eventLog.add("💡 LEMBRETE: Viagens consomem suprimentos e desgastam o navio. Gerencie seus recursos!");
        }
        
        eventLog.addAll(travelCosts); // Adicionar custos de viagem ao log
        eventLog.add(encounterMessage);
        eventLog.addAll(progressMessages);

        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());

        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    private SeaEncounter generateRandomEncounter(Game game) {
        // Usa o novo serviço para determinar o tipo baseado no contrato ativo
        SeaEncounterType encounterType = contractEncounterService.generateEncounterType(game);
        
        SeaEncounter.SeaEncounterBuilder encounterBuilder = SeaEncounter.builder().type(encounterType);

        switch (encounterType) {
            // Encontros básicos originais
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
            
            // Encontros relacionados a contratos da GUILD
            case GUILD_CONVOY -> {
                encounterBuilder
                    .description("Um comboio da Guilda Mercante navega pesadamente, escoltado por navios de guerra. Suas cargas podem ser valiosas...")
                    .hull(75)
                    .cannons(6)
                    .sails(5);
            }
            case TRADE_DISPUTE -> {
                encounterBuilder
                    .description("Dois navios mercantes estão parados, aparentemente em uma disputa comercial acalorada. Eles podem precisar de mediação.")
                    .hull(0)
                    .cannons(0)
                    .sails(0);
            }
            case MERCHANT_DISTRESS -> {
                encounterBuilder
                    .description("Um navio da Guilda está enviando sinais de socorro. Fumaça sobe de seus compartimentos de carga.")
                    .hull(0)
                    .cannons(0)
                    .sails(0);
            }
            
            // Encontros relacionados a contratos do EMPIRE
            case IMPERIAL_ESCORT -> {
                encounterBuilder
                    .description("Uma frota de escolta imperial transporta algo de grande valor. Seus navios estão em formação de combate.")
                    .hull(100)
                    .cannons(10)
                    .sails(6);
            }
            case REBEL_SABOTEURS -> {
                encounterBuilder
                    .description("Navios disfarçados de mercadores revelam suas verdadeiras intenções - são sabotadores rebeldes!")
                    .hull(60)
                    .cannons(6)
                    .sails(8);
            }
            case TAX_COLLECTORS -> {
                encounterBuilder
                    .description("Coletores de impostos imperiais abordam navios para inspeções 'de rotina'. Sua autoridade é absoluta.")
                    .hull(90)
                    .cannons(8)
                    .sails(6);
            }
            
            // Encontros relacionados a contratos da BROTHERHOOD
            case SMUGGLER_MEET -> {
                encounterBuilder
                    .description("Um navio sem bandeira se aproxima discretamente. O capitão sussurra sobre 'negócios especiais'.")
                    .hull(65)
                    .cannons(4)
                    .sails(9);
            }
            case IMPERIAL_PURSUIT -> {
                encounterBuilder
                    .description("Navios imperiais em perseguição quente! Eles procuram contrabandistas e qualquer um pode ser suspeito.")
                    .hull(110)
                    .cannons(10)
                    .sails(7);
            }
            case PIRATE_ALLIANCE -> {
                encounterBuilder
                    .description("Um capitão pirata respeitado oferece uma proposta de aliança. Suas intenções podem ser honestas... ou não.")
                    .hull(85)
                    .cannons(8)
                    .sails(7);
            }
            
            // Encontros relacionados a contratos REVOLUTIONARY
            case FREEDOM_FIGHTERS -> {
                encounterBuilder
                    .description("Lutadores pela liberdade em navios improvisados pedem sua ajuda contra a opressão imperial.")
                    .hull(45)
                    .cannons(3)
                    .sails(6);
            }
            case IMPERIAL_OPPRESSION -> {
                encounterBuilder
                    .description("Você testemunha um ato de brutalidade imperial contra civis. A justiça clama por ação.")
                    .hull(0)
                    .cannons(0)
                    .sails(0);
            }
            case UNDERGROUND_NETWORK -> {
                encounterBuilder
                    .description("Um contato da rede clandestina se aproxima com informações valiosas sobre movimentos imperiais.")
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

        actions.add(new PortActionDTO(
                PortActionType.VIEW_CAPTAIN_SKILLS,
                "⭐ Progressão do Capitão",
                "Veja suas habilidades e invista pontos de experiência.",
                "/api/games/" + gameId + "/captain/progression"
        ));

        // Adicionar ação de resolver contrato se estiver no porto correto
        Contract activeContract = game.getActiveContract();
        if (activeContract != null && activeContract.getDestinationPort().equals(game.getCurrentPort())) {
            actions.add(new PortActionDTO(
                    PortActionType.RESOLVE_CONTRACT,
                    "✅ Resolver Contrato",
                    String.format("Completar o contrato '%s' e receber as recompensas.", activeContract.getTitle()),
                    "/api/games/" + gameId + "/contracts/resolve"
            ));
        }

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

        // Usar o serviço para determinar se é encontro de combate ou narrativo
        boolean isCombatEncounter = contractEncounterService.isCombatEncounter(encounter.getType());
        
        if (isCombatEncounter) {
            // Ações padrão de combate
            actions.add(new EncounterActionDTO(EncounterActionType.ATTACK, "Atacar", "Iniciar combate naval.", basePath + "attack"));
            actions.add(new EncounterActionDTO(EncounterActionType.BOARD, "Abordar", "Tentar uma abordagem para capturar o navio.", basePath + "board"));
            actions.add(new EncounterActionDTO(EncounterActionType.FLEE, "Fugir", "Tentar escapar do encontro.", basePath + "flee"));
            
            // Ações específicas por tipo
            switch (encounter.getType()) {
                case MERCHANT_SHIP, GUILD_CONVOY, MERCHANT_DISTRESS -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.NEGOTIATE, "Negociar", "Tentar uma abordagem diplomática ou comercial.", basePath + "negotiate"));
                }
                case SMUGGLER_MEET, PIRATE_ALLIANCE -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.NEGOTIATE, "Negociar", "Discutir termos de cooperação.", basePath + "negotiate"));
                }
                case FREEDOM_FIGHTERS -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.NEGOTIATE, "Apoiar", "Oferecer apoio à causa da liberdade.", basePath + "negotiate"));
                }
            }
        } else {
            // Encontros narrativos específicos
            switch (encounter.getType()) {
                case MYSTERIOUS_WRECK -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.INVESTIGATE, "Investigar", "Explorar os destroços em busca de recursos ou sobreviventes.", basePath + "investigate"));
                }
                case TRADE_DISPUTE -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.INVESTIGATE, "Mediar", "Tentar resolver a disputa comercial.", basePath + "investigate"));
                }
                case IMPERIAL_OPPRESSION -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.INVESTIGATE, "Intervir", "Tentar intervir contra a injustiça.", basePath + "investigate"));
                    actions.add(new EncounterActionDTO(EncounterActionType.FLEE, "Ignorar", "Seguir viagem sem se envolver.", basePath + "flee"));
                }
                case UNDERGROUND_NETWORK -> {
                    actions.add(new EncounterActionDTO(EncounterActionType.INVESTIGATE, "Escutar", "Ouvir as informações oferecidas.", basePath + "investigate"));
                }
            }
        }

        return actions;
    }

    
    public Game recruitCrewMember(Long gameId, RecruitCrewMemberRequest request) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();

        // Verificar se há espaço na tripulação
        int currentCrewSize = ship.getCrew().size();
        int maxCapacity = captainProgressionService.getMaxCrewCapacity(game);
        
        if (currentCrewSize >= maxCapacity) {
            throw new IllegalStateException(String.format(
                "Tripulação lotada! Você já tem %d/%d tripulantes. Evolua a habilidade 'Liderança' do capitão para aumentar a capacidade.",
                currentCrewSize, maxCapacity));
        }

        // Calcular custos ANTES de criar o tripulante
        int attributeSum = request.getNavigation() + request.getArtillery() + request.getCombat() +
                           request.getMedicine() + request.getCarpentry() + request.getIntelligence();
        int baseSalary = 20; // Aumentado de 10 para 20 para maior pressão econômica
        int salary = baseSalary + (attributeSum / 8) - request.getDespairLevel(); // Divisor reduzido de 10 para 8
        salary = Math.max(5, salary); // Salário mínimo aumentado de 1 para 5
        
        // Custo de contratação: 3x o salário mensal + bônus por atributos altos
        int hiringCost = (salary * 3) + (attributeSum > 45 ? (attributeSum - 45) * 10 : 0);
        
        // Verificar se o jogador tem dinheiro suficiente
        if (game.getGold() < hiringCost) {
            throw new IllegalStateException(String.format(
                "Dinheiro insuficiente para contratar %s. Custo: %d moedas, Disponível: %d moedas", 
                request.getName(), hiringCost, game.getGold()));
        }

        CrewMember newCrewMember = CrewMember.builder()
                .name(request.getName()).background(request.getBackground()).catchphrase(request.getCatchphrase())
                .personality(request.getPersonality()).despairLevel(request.getDespairLevel())
                .navigation(request.getNavigation()).artillery(request.getArtillery()).combat(request.getCombat())
                .medicine(request.getMedicine()).carpentry(request.getCarpentry()).intelligence(request.getIntelligence())
                .ship(ship).build();

        int baseMoral = 50;
        int personalityModifier = request.getPersonality().getMoralModifier() * 5;
        int despairPenalty = request.getDespairLevel() * 2;
        int initialMoral = baseMoral + personalityModifier - despairPenalty;
        newCrewMember.setMoral(Math.max(0, Math.min(100, initialMoral)));
        newCrewMember.setSalary(salary);

        // Determinar profissão inicial baseada nos atributos
        newCrewMember.updateProfession();

        // Cobrar o custo de contratação
        game.setGold(game.getGold() - hiringCost);
        
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

        // Conceder XP do capitão por completar contrato
        List<String> captainXPMessages = crewProgressionService.awardContractXP(game);
        eventLog.addAll(captainXPMessages);

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

            // Notificar tutorial sobre a chegada ao destino (se em tutorial)
            if (!game.isTutorialCompleted() && game.getTutorialPhase() == TutorialPhase.JOURNEY_EVENT) {
                System.out.println("DEBUG: GameService fleeEncounter - Enviando notificação ARRIVE_DESTINATION para tutorial do game " + game.getId());
                tutorialService.progressTutorial(game.getId(), 
                    TutorialProgressRequestDTO.builder().action("ARRIVE_DESTINATION").build());
            } else {
                System.out.println("DEBUG: GameService fleeEncounter - Não enviando notificação. TutorialCompleted: " + game.isTutorialCompleted() + ", Fase: " + game.getTutorialPhase());
            }
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
        if (encounter == null || contractEncounterService.isCombatEncounter(encounter.getType())) {
            throw new IllegalStateException("A ação 'Investigar' só pode ser usada em encontros narrativos.");
        }

        Port destination = game.getDestinationPort();
        if (destination == null) {
            throw new IllegalStateException("Não há um destino definido para o qual viajar após a investigação.");
        }

        // Processar o encontro específico baseado no tipo
        processNarrativeEncounter(game, encounter, eventLog);

        // Verificar se o jogo terminou durante o processamento
        if (game.isGameOver()) {
            Game savedGame = gameRepository.save(game);
            return GameActionResponseDTO.builder()
                    .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                    .eventLog(eventLog)
                    .gameOver(true)
                    .build();
        }

        game.setCurrentPort(destination);
        game.setCurrentEncounter(null);
        game.setDestinationPort(null);

        endTurnCycle(game, eventLog);

        // Notificar tutorial sobre a chegada ao destino (se em tutorial)
        if (!game.isTutorialCompleted() && game.getTutorialPhase() == TutorialPhase.JOURNEY_EVENT) {
            System.out.println("DEBUG: GameService investigateEncounter - Enviando notificação ARRIVE_DESTINATION para tutorial do game " + game.getId());
            tutorialService.progressTutorial(game.getId(), 
                TutorialProgressRequestDTO.builder().action("ARRIVE_DESTINATION").build());
        } else {
            System.out.println("DEBUG: GameService investigateEncounter - Não enviando notificação. TutorialCompleted: " + game.isTutorialCompleted() + ", Fase: " + game.getTutorialPhase());
        }

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

        if (encounter == null || !contractEncounterService.isCombatEncounter(encounter.getType())) {
            throw new IllegalStateException("Não há um alvo válido para atacar.");
        }

        int playerArtillery = ship.getCrew().stream().mapToInt(CrewMember::getArtillery).sum();
        
        // Aplicar bônus de habilidades do capitão no combate
        double artilleryMultiplier = captainProgressionService.getArtilleryBonus(game);
        double combatMultiplier = captainProgressionService.getCombatBonus(game);
        
        int baseDamage = playerArtillery + ThreadLocalRandom.current().nextInt(5, 11);
        int finalDamage = (int) Math.round(baseDamage * artilleryMultiplier * combatMultiplier);
        
        encounter.setHull(encounter.getHull() - finalDamage);
        
        if (artilleryMultiplier > 1.0 || combatMultiplier > 1.0) {
            eventLog.add(String.format("Você ordena o ataque! Seus artilheiros (Habilidade %d) disparam uma salva devastadora, causando %d de dano ao casco inimigo! (Bônus do capitão aplicado)", playerArtillery, finalDamage));
        } else {
            eventLog.add(String.format("Você ordena o ataque! Seus artilheiros (Habilidade %d) disparam uma salva de canhões, causando %d de dano ao casco inimigo.", playerArtillery, finalDamage));
        }

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
            
            // Aplicar bônus de contrato se aplicável
            int contractGoldReward = applyContractBonus(game, encounter.getType(), goldReward, eventLog);
            
            // Aplicar bônus de liderança do capitão
            double leadershipBonus = captainProgressionService.getLeadershipBonus(game);
            int finalGoldReward = (int) Math.round(contractGoldReward * leadershipBonus);
            
            if (leadershipBonus > 1.0) {
                eventLog.add(String.format("Sua liderança inspiradora motiva a tripulação a encontrar mais tesouros! Bônus: +%d de ouro.", finalGoldReward - contractGoldReward));
            }
            
            game.setGold(game.getGold() + finalGoldReward);

            // Conceder XP de combate para a tripulação e capitão (vitória)
            List<String> progressMessages = crewProgressionService.awardCombatXP(game, true, false);
            eventLog.addAll(progressMessages);
            
            List<String> captainProgressMessages = captainProgressionService.awardCaptainXP(game, 50, "vitória em combate");
            eventLog.addAll(captainProgressMessages);

            Port destination = game.getDestinationPort();
            game.setCurrentPort(destination);
            game.setCurrentEncounter(null);
            game.setDestinationPort(null);
            eventLog.add("Com a batalha terminada, você continua sua viagem e chega a " + destination.getName() + ".");
            endTurnCycle(game, eventLog);

            // Notificar tutorial sobre a chegada ao destino (se em tutorial)
            if (!game.isTutorialCompleted() && game.getTutorialPhase() == TutorialPhase.JOURNEY_EVENT) {
                tutorialService.progressTutorial(game.getId(), 
                    TutorialProgressRequestDTO.builder().action("ARRIVE_DESTINATION").build());
            }

        } else {
            // Conceder XP de combate para a tripulação e capitão (participação, sem vitória)
            List<String> progressMessages = crewProgressionService.awardCombatXP(game, false, false);
            eventLog.addAll(progressMessages);
            
            List<String> captainProgressMessages = captainProgressionService.awardCaptainXP(game, 15, "participação em combate");
            eventLog.addAll(captainProgressMessages);
            
            int enemyDamage = encounter.getCannons() + ThreadLocalRandom.current().nextInt(1, 7);
            ship.setHullIntegrity(ship.getHullIntegrity() - enemyDamage);
            eventLog.add(String.format("O inimigo revida! Os canhões deles causam %d de dano ao seu casco.", enemyDamage));
            
            // Verificar se o navio foi destruído
            if (ship.getHullIntegrity() <= 0) {
                ship.setHullIntegrity(0); // Garantir que não fique negativo
                eventLog.add("💀 DERROTA TOTAL! Seu navio foi destruído!");
                eventLog.add("O casco se parte sob o bombardeio inimigo. Água salgada invade os compartimentos.");
                eventLog.add("Sua tripulação luta desesperadamente, mas não há como salvar o navio...");
                eventLog.add("🌊 SEU NAVIO AFUNDOU - FIM DE JOGO 🌊");
                
                // Marcar o jogo como terminado
                game.setGameOver(true);
                game.setGameOverReason("Navio destruído em combate");
                
                Game savedGame = gameRepository.save(game);
                return GameActionResponseDTO.builder()
                        .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                        .eventLog(eventLog)
                        .gameOver(true)
                        .build();
            }
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

        if (encounter == null || !contractEncounterService.isCombatEncounter(encounter.getType())) {
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

            // Notificar tutorial sobre a chegada ao destino (se em tutorial)
            if (!game.isTutorialCompleted() && game.getTutorialPhase() == TutorialPhase.JOURNEY_EVENT) {
                tutorialService.progressTutorial(game.getId(), 
                    TutorialProgressRequestDTO.builder().action("ARRIVE_DESTINATION").build());
            }

        } else {
            int hullDamage = ThreadLocalRandom.current().nextInt(10, 21);
            int moralePenalty = -15;
            ship.setHullIntegrity(ship.getHullIntegrity() - hullDamage);
            ship.getCrew().forEach(member -> member.setMoral(member.getMoral() + moralePenalty));

            eventLog.add(String.format("DERROTA! Sua tripulação foi repelida! Em meio à retirada caótica, seu navio sofre %d de dano ao casco e a moral da tripulação despenca.", hullDamage));
            
            // Verificar se o navio foi destruído
            if (ship.getHullIntegrity() <= 0) {
                ship.setHullIntegrity(0);
                eventLog.add("💀 DERROTA TOTAL! Seu navio foi destruído!");
                eventLog.add("O dano estrutural é irreparável. Água do mar invade o compartimento principal.");
                eventLog.add("Sua tripulação abandona o navio que está afundando...");
                eventLog.add("🌊 SEU NAVIO AFUNDOU - FIM DE JOGO 🌊");
                
                game.setGameOver(true);
                game.setGameOverReason("Navio destruído durante tentativa de abordagem");
                
                Game savedGame = gameRepository.save(game);
                return GameActionResponseDTO.builder()
                        .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                        .eventLog(eventLog)
                        .gameOver(true)
                        .build();
            }
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
    public GameActionResponseDTO healCrew(Long gameId) {
        Game game = findGameById(gameId);
        Ship ship = game.getShip();
        SeaEncounter encounter = game.getCurrentEncounter();
        List<String> eventLog = new ArrayList<>();

        if (encounter == null) {
            throw new IllegalStateException("Não há combate ativo para aplicar cuidados médicos.");
        }

        // Calcular poder de cura baseado na medicina total da tripulação
        int totalMedicine = ship.getCrew().stream()
            .mapToInt(CrewMember::getMedicine)
            .sum();
        
        if (totalMedicine == 0) {
            eventLog.add("MÉDICOS INSUFICIENTES! Sua tripulação não possui conhecimento médico adequado para tratar ferimentos.");
            Game savedGame = gameRepository.save(game);
            return GameActionResponseDTO.builder()
                    .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame))
                    .eventLog(eventLog)
                    .build();
        }

        // Calcular quantidade de cura baseada na medicina
        int maxHeal = ship.getMaxHullIntegrity() - ship.getHullIntegrity();
        int basehealAmount = totalMedicine / 2; // 50% da medicina se converte em cura
        int healAmount = Math.min(basehealAmount, maxHeal);
        
        if (healAmount > 0) {
            ship.setHullIntegrity(ship.getHullIntegrity() + healAmount);
            
            // Determinar qualidade do tratamento para XP
            boolean significantHealing = healAmount >= 15;
            
            if (totalMedicine >= 25) {
                eventLog.add(String.format("CUIDADOS EXCEPCIONAIS! Seus médicos experientes (Medicina Total: %d) trataram ferimentos críticos da tripulação, restaurando %d pontos de integridade!", totalMedicine, healAmount));
            } else if (totalMedicine >= 15) {
                eventLog.add(String.format("Cuidados médicos competentes! Seus curandeiros (Medicina Total: %d) estancaram ferimentos e repararam danos menores, recuperando %d pontos de integridade.", totalMedicine, healAmount));
            } else {
                eventLog.add(String.format("Primeiros socorros básicos aplicados. Com medicina limitada (Total: %d), sua tripulação conseguiu recuperar %d pontos de integridade.", totalMedicine, healAmount));
            }
            
            // Conceder XP médico
            List<String> progressMessages = crewProgressionService.awardMedicalXP(game, significantHealing);
            eventLog.addAll(progressMessages);
            
            // Melhorar moral da tripulação quando recebem cuidados médicos
            ship.getCrew().forEach(member -> {
                int moralBoost = Math.min(10, totalMedicine / 3);
                member.setMoral(Math.min(100, member.getMoral() + moralBoost));
            });
            eventLog.add("O moral da tripulação se eleva ao ver os ferimentos sendo tratados com cuidado.");
            
        } else {
            eventLog.add("NAVIO EM PERFEITAS CONDIÇÕES! Não há ferimentos ou danos que requeiram atenção médica no momento.");
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
        
        // Conceder XP de carpintaria para carpinteiros
        boolean emergencyRepair = damage >= (ship.getMaxHullIntegrity() * 0.5); // 50% ou mais de dano
        List<String> progressMessages = crewProgressionService.awardRepairXP(game, emergencyRepair);
        eventLog.addAll(progressMessages);

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
        Random random = new Random(gameId + currentPort.getId()); // Seed para preços consistentes por jogo/porto
        
        // Aplicar variação de ±20% nos preços base
        int foodPrice = applyPriceVariation(currentPort.getFoodPrice(), random);
        int rumPrice = applyPriceVariation(currentPort.getRumPrice(), random);
        int toolsPrice = applyPriceVariation(currentPort.getToolsPrice(), random);
        int shotPrice = applyPriceVariation(currentPort.getShotPrice(), random);
        
        return MarketDTO.builder()
                .foodPrice(foodPrice)
                .rumPrice(rumPrice)
                .toolsPrice(toolsPrice)
                .shotPrice(shotPrice)
                .shipFood(ship.getFoodRations())
                .shipRum(ship.getRumRations())
                .shipTools(ship.getRepairParts())
                .shipShot(ship.getShot())
                .shipGold(game.getGold())
                .build();
    }
    
    /**
     * Aplica variação de ±20% ao preço base
     */
    private int applyPriceVariation(int basePrice, Random random) {
        double variation = 0.8 + (random.nextDouble() * 0.4); // Entre 0.8 e 1.2 (±20%)
        return Math.max(1, (int) Math.round(basePrice * variation));
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
        Random random = new Random(gameId + currentPort.getId()); // Mesmo seed para consistência
        int pricePerUnit = 0;

        switch (request.getItem()) {
            case FOOD -> pricePerUnit = applyPriceVariation(currentPort.getFoodPrice(), random);
            case RUM -> pricePerUnit = applyPriceVariation(currentPort.getRumPrice(), random);
            case TOOLS -> pricePerUnit = applyPriceVariation(currentPort.getToolsPrice(), random);
            case SHOT -> pricePerUnit = applyPriceVariation(currentPort.getShotPrice(), random);
        }

        // Aplicar desconto por habilidades de negociação do capitão
        double tradingBonus = captainProgressionService.getTradingBonus(game);
        int finalCost = (int) Math.round(pricePerUnit * request.getQuantity() / tradingBonus);
        
        if (game.getGold() < finalCost) {
            throw new IllegalStateException(String.format("Ouro insuficiente. Você precisa de %d, mas tem apenas %d.", finalCost, game.getGold()));
        }

        game.setGold(game.getGold() - finalCost);
        
        if (tradingBonus > 1.0) {
            int savings = (pricePerUnit * request.getQuantity()) - finalCost;
            eventLog.add(String.format("Suas habilidades de negociação economizaram %d de ouro na compra!", savings));
        }

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

        // Aplicar bônus por habilidades de negociação do capitão
        double tradingBonus = captainProgressionService.getTradingBonus(game);
        int finalGain = (int) Math.round(pricePerUnit * request.getQuantity() * tradingBonus);
        
        game.setGold(game.getGold() + finalGain);
        
        if (tradingBonus > 1.0) {
            int bonus = finalGain - (pricePerUnit * request.getQuantity());
            eventLog.add(String.format("Suas habilidades de negociação renderam %d de ouro extra na venda!", bonus));
        }

        switch (request.getItem()) {
            case FOOD -> ship.setFoodRations(ship.getFoodRations() - request.getQuantity());
            case RUM -> ship.setRumRations(ship.getRumRations() - request.getQuantity());
            case TOOLS -> ship.setRepairParts(ship.getRepairParts() - request.getQuantity());
            case SHOT -> ship.setShot(ship.getShot() - request.getQuantity());
        }

        eventLog.add(String.format("Você vendeu %d unidades de %s por %d de ouro.", request.getQuantity(), request.getItem(), finalGain));

        Game savedGame = gameRepository.save(game);
        List<PortActionDTO> portActions = getAvailablePortActions(savedGame.getId());
        List<EncounterActionDTO> encounterActions = getAvailableEncounterActions(savedGame.getId());
        return GameActionResponseDTO.builder()
                .gameStatus(gameMapper.toGameStatusResponseDTO(savedGame, portActions, encounterActions))
                .eventLog(eventLog)
                .build();
    }

    @Transactional(readOnly = true)
    public TavernInfoDTO getTavernInfo(Long gameId) {
        Game game = findGameById(gameId);
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            throw new IllegalStateException("O navio deve estar em um porto para visitar a taverna.");
        }

        List<TavernRecruitDTO> recruits = new ArrayList<>();
        int numberOfRecruits = 3; // Sempre 3 recrutas disponíveis

        // Gerar personagens únicos específicos para este tipo de porto
        // Usa seed baseado no gameId e número de tripulantes para gerar lista consistente mas renovável
        int currentCrewSize = game.getShip().getCrew().size();
        long seed = gameId + currentPort.getId() + (currentCrewSize * 1000L); // Muda quando tripulação muda
        
        // Se ainda não completou o tutorial, considera a escolha inicial para personalidades
        List<RecruitCrewMemberRequest> uniqueCharacters;
        if (!game.isTutorialCompleted() && game.getIntroChoice() != null) {
            uniqueCharacters = uniqueCharacterService.generateTavernCharactersWithSeed(
                currentPort.getType(), numberOfRecruits, game.getIntroChoice(), seed);
        } else {
            uniqueCharacters = uniqueCharacterService.generateTavernCharactersWithSeed(
                currentPort.getType(), numberOfRecruits, seed);
        }

        for (RecruitCrewMemberRequest character : uniqueCharacters) {
            int attributeSum = character.getNavigation() + character.getArtillery() + character.getCombat() +
                             character.getMedicine() + character.getCarpentry() + character.getIntelligence();
            
            int salary = Math.max(5, 20 + (attributeSum / 8) - character.getDespairLevel());
            int hiringCost = (salary * 3) + (attributeSum > 45 ? (attributeSum - 45) * 10 : 0);
            int initialMoral = Math.max(0, Math.min(100, 50 + (character.getPersonality().getMoralModifier() * 5) - (character.getDespairLevel() * 2)));

            // Determinar profissão baseada nos atributos
            CrewProfession profession = CrewProfession.determineProfession(
                character.getNavigation(), character.getArtillery(), character.getCombat(),
                character.getMedicine(), character.getCarpentry(), character.getIntelligence()
            );

            // Encontrar atributo principal
            int primaryAttribute = switch (profession) {
                case NAVIGATOR -> character.getNavigation();
                case GUNNER -> character.getArtillery();
                case FIGHTER -> character.getCombat();
                case MEDIC -> character.getMedicine();
                case CARPENTER -> character.getCarpentry();
                case STRATEGIST -> character.getIntelligence();
                case CORSAIR -> Math.max(character.getCombat(), character.getArtillery());
                case EXPLORER -> Math.max(character.getNavigation(), character.getIntelligence());
                case BATTLE_MEDIC -> Math.max(character.getMedicine(), character.getCombat());
                case SAILOR -> attributeSum / 6;
            };

            // Usar background e catchphrase dos personagens únicos
            String background = character.getBackground() != null ? character.getBackground() : "História pessoal a ser revelada...";
            String catchphrase = character.getCatchphrase() != null ? character.getCatchphrase() : "\"...\"";
            String specialization = profession.getDisplayName() + " Nível " + primaryAttribute;

            TavernRecruitDTO dto = TavernRecruitDTO.builder()
                    .name(character.getName())
                    .background(background)
                    .catchphrase(catchphrase)
                    .personality(character.getPersonality())
                    .despairLevel(character.getDespairLevel())
                    .salary(salary)
                    .hiringCost(hiringCost)
                    .initialMoral(initialMoral)
                    .profession(profession.getDisplayName())
                    .professionDescription(profession.getDescription())
                    .professionIcon(profession.getIcon())
                    .professionColor(profession.getColor())
                    .primaryAttribute(primaryAttribute)
                    .specialization(specialization)
                    .navigation(character.getNavigation())
                    .artillery(character.getArtillery())
                    .combat(character.getCombat())
                    .medicine(character.getMedicine())
                    .carpentry(character.getCarpentry())
                    .intelligence(character.getIntelligence())
                    .recruitRequest(character)
                    .build();

            recruits.add(dto);
        }

        // Calcular informações da capacidade da tripulação
        int maxCapacity = captainProgressionService.getMaxCrewCapacity(game);
        boolean canRecruitMore = currentCrewSize < maxCapacity;
        
        String tavernMessage;
        if (!canRecruitMore) {
            tavernMessage = String.format("Sua tripulação está lotada (%d/%d). Evolva a habilidade 'Liderança' do capitão para contratar mais tripulantes.", 
                currentCrewSize, maxCapacity);
        } else {
            tavernMessage = String.format("Tripulação atual: %d/%d. Você pode contratar mais %d tripulante(s).", 
                currentCrewSize, maxCapacity, maxCapacity - currentCrewSize);
        }
        
        return TavernInfoDTO.builder()
                .currentCrewSize(currentCrewSize)
                .maxCrewCapacity(maxCapacity)
                .canRecruitMore(canRecruitMore)
                .availableRecruits(recruits)
                .tavernMessage(tavernMessage)
                .build();
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
        
        // Aceitar automaticamente o contrato correspondente à escolha inicial
        try {
            Contract introContract = findIntroContract(game, introChoiceEnum);
            if (introContract != null) {
                game.setActiveContract(introContract);
                introContract.setGame(game);
                introContract.setStatus(ContractStatus.IN_PROGRESS);
                contractRepository.save(introContract);
                
                eventMessages.add(String.format("Contrato ativo: %s", introContract.getTitle()));
            }
        } catch (Exception e) {
            // Se não conseguir aceitar o contrato, continua sem ele
            eventMessages.add("Aviso: Não foi possível ativar o contrato inicial automaticamente.");
        }
        
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

    /**
     * Retorna informações detalhadas de gerenciamento da tripulação.
     */
    public CrewManagementDTO getCrewManagement(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        List<CrewMember> crewMembers = game.getShip().getCrew();
        
        // Construir DTOs detalhados para cada tripulante
        List<CrewMemberDetailDTO> crewMemberDetails = crewMembers.stream()
                .map(this::buildCrewMemberDetailDTO)
                .collect(Collectors.toList());

        // Calcular estatísticas gerais
        double averageMorale = crewMembers.stream()
                .mapToInt(CrewMember::getMoral)
                .average()
                .orElse(0.0);

        int totalSalaryExpenses = crewMembers.stream()
                .mapToInt(CrewMember::getSalary)
                .sum();

        int totalXPEarned = crewMembers.stream()
                .mapToInt(CrewMember::getExperiencePoints)
                .sum();

        // Calcular resumos por profissão
        List<ProfessionSummaryDTO> professionSummaries = crewMembers.stream()
                .collect(Collectors.groupingBy(CrewMember::getProfession))
                .entrySet().stream()
                .map(entry -> {
                    CrewProfession profession = entry.getKey();
                    List<CrewMember> membersOfProfession = entry.getValue();
                    
                    double averageRankLevel = membersOfProfession.stream()
                            .mapToInt(member -> member.getRank().ordinal() + 1)
                            .average()
                            .orElse(0.0);
                            
                    int totalXP = membersOfProfession.stream()
                            .mapToInt(CrewMember::getExperiencePoints)
                            .sum();
                    
                    return ProfessionSummaryDTO.builder()
                            .profession(profession.getDisplayName())
                            .professionIcon(profession.getIcon())
                            .professionColor(profession.getColor())
                            .memberCount(membersOfProfession.size())
                            .averageRankLevel(averageRankLevel)
                            .totalXP(totalXP)
                            .build();
                })
                .collect(Collectors.toList());

        return CrewManagementDTO.builder()
                .totalCrewMembers(crewMembers.size())
                .averageMorale(averageMorale)
                .totalSalaryExpenses(totalSalaryExpenses)
                .totalXPEarned(totalXPEarned)
                .totalCombatsParticipated(crewMembers.stream().mapToInt(CrewMember::getCombatsParticipated).sum())
                .totalRepairsCompleted(crewMembers.stream().mapToInt(CrewMember::getRepairsPerformed).sum())
                .totalNavigationsCompleted(crewMembers.stream().mapToInt(CrewMember::getJourneysCompleted).sum())
                .totalContractsCompleted(crewMembers.stream().mapToInt(member -> 0).sum()) // Não há campo específico para contratos
                .crewMembers(crewMemberDetails)
                .professionSummaries(professionSummaries)
                .build();
    }

    private CrewMemberDetailDTO buildCrewMemberDetailDTO(CrewMember member) {
        CrewProfession profession = member.getProfession();
        CrewRank currentRank = member.getRank();
        CrewRank nextRank = currentRank.getNextRank(profession);
        
        int xpForCurrentRank = currentRank.getRequiredXP();
        int xpForNextRank = nextRank != null ? nextRank.getRequiredXP() : currentRank.getRequiredXP();
        int currentXP = member.getExperiencePoints();
        
        // Calcular progresso de XP (0.0 a 1.0)
        double xpProgress = 0.0;
        if (nextRank != null && xpForNextRank > xpForCurrentRank) {
            int xpInCurrentLevel = currentXP - xpForCurrentRank;
            int xpNeededForNext = xpForNextRank - xpForCurrentRank;
            xpProgress = Math.min(1.0, Math.max(0.0, (double) xpInCurrentLevel / xpNeededForNext));
        }

        // Lista de habilidades desbloqueadas baseada no rank atual
        List<String> unlockedAbilities = List.of(
                String.format("Rank %s: %s", currentRank.getDisplayName(), currentRank.getDescription()),
                String.format("Bônus de Atributos: +%d pontos", currentRank.ordinal()),
                String.format("Experiência em %s", profession.getDescription())
        );

        return CrewMemberDetailDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .background(member.getBackground())
                .catchphrase(member.getCatchphrase())
                .personality(member.getPersonality())
                .salary(member.getSalary())
                .morale(member.getMoral())
                .navigation(member.getNavigation())
                .artillery(member.getArtillery())
                .combat(member.getCombat())
                .medicine(member.getMedicine())
                .carpentry(member.getCarpentry())
                .intelligence(member.getIntelligence())
                .profession(profession)
                .professionIcon(profession.getIcon())
                .professionColor(profession.getColor())
                .currentRank(currentRank)
                .nextRank(nextRank)
                .currentXP(currentXP)
                .xpForCurrentRank(xpForCurrentRank)
                .xpForNextRank(xpForNextRank)
                .xpProgress(xpProgress)
                .unlockedAbilities(unlockedAbilities)
                .rankDescription(currentRank.getDescription())
                .totalCombats(member.getCombatsParticipated())
                .totalRepairs(member.getRepairsPerformed())
                .totalNavigations(member.getJourneysCompleted())
                .totalContracts(0) // Campo não existe ainda no modelo
                .build();
    }
    
    /**
     * Processa encontros narrativos específicos com lógica personalizada para cada tipo.
     */
    private void processNarrativeEncounter(Game game, SeaEncounter encounter, List<String> eventLog) {
        Ship ship = game.getShip();
        
        switch (encounter.getType()) {
            case MYSTERIOUS_WRECK -> processMysteruousWreck(game, eventLog);
            case TRADE_DISPUTE -> processTradeDispute(game, eventLog);
            case MERCHANT_DISTRESS -> processMerchantDistress(game, eventLog);
            case IMPERIAL_OPPRESSION -> processImperialOppression(game, eventLog);
            case UNDERGROUND_NETWORK -> processUndergroundNetwork(game, eventLog);
            default -> {
                // Fallback para encontros não implementados
                eventLog.add("Você observa a situação cuidadosamente, mas decide não se envolver.");
                eventLog.add("Às vezes, a prudência é a melhor política no mar.");
            }
        }
    }
    
    private void processMysteruousWreck(Game game, List<String> eventLog) {
        Ship ship = game.getShip();
        eventLog.add("Você ordena que a tripulação investigue os destroços...");

        // Risco de dano no navio
        if (ThreadLocalRandom.current().nextDouble() < 0.15) {
            int hullDamage = ThreadLocalRandom.current().nextInt(3, 8);
            ship.setHullIntegrity(ship.getHullIntegrity() - hullDamage);
            eventLog.add(String.format("RISCO: Ao se aproximar, destroços ocultos arranham o casco, causando %d de dano!", hullDamage));
            
            if (ship.getHullIntegrity() <= 0) {
                ship.setHullIntegrity(0);
                eventLog.add("💀 CATÁSTROFE! O dano foi crítico!");
                eventLog.add("Os destroços perfuraram completamente o casco. Água jorra para dentro do navio.");
                eventLog.add("Não há tempo para reparos... o navio está perdido!");
                eventLog.add("🌊 SEU NAVIO AFUNDOU - FIM DE JOGO 🌊");
                game.setGameOver(true);
                game.setGameOverReason("Navio destruído por destroços durante investigação");
                return;
            }
        }

        // Teste de inteligência para encontrar tesouros
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
        
        Port destination = game.getDestinationPort();
        eventLog.add("Com os destroços vasculhados, você continua sua viagem e chega a " + destination.getName() + ".");
    }
    
    private void processTradeDispute(Game game, List<String> eventLog) {
        eventLog.add("Você se aproxima dos navios mercantes em disputa e oferece mediação...");
        
        // Teste de reputação para mediar com sucesso
        int reputation = game.getReputation();
        boolean success = reputation >= 50 || ThreadLocalRandom.current().nextDouble() < 0.6;
        
        if (success) {
            int goldReward = ThreadLocalRandom.current().nextInt(50, 101);
            game.setGold(game.getGold() + goldReward);
            game.setReputation(Math.min(1000, game.getReputation() + 10));
            eventLog.add("MEDIAÇÃO BEM-SUCEDIDA! Sua intervenção resolve o conflito pacificamente.");
            eventLog.add(String.format("Os mercadores agradecem com %d de ouro e sua reputação cresce.", goldReward));
        } else {
            eventLog.add("MEDIAÇÃO FALHOU! Os mercadores se recusam a ouvir um capitão desconhecido.");
            eventLog.add("Você continua sua viagem sem conseguir ajudar, mas pelo menos tentou fazer o certo.");
        }
        
        Port destination = game.getDestinationPort();
        eventLog.add("Deixando os mercadores para trás, você continua em direção a " + destination.getName() + ".");
    }
    
    private void processMerchantDistress(Game game, List<String> eventLog) {
        eventLog.add("Você responde aos sinais de socorro do mercador da Guilda...");
        
        // Decisão baseada no tipo de ajuda oferecida
        boolean hasSupplies = game.getShip().getFoodRations() >= 5;
        
        if (hasSupplies) {
            // Dar suprimentos
            game.getShip().setFoodRations(game.getShip().getFoodRations() - 5);
            int goldReward = ThreadLocalRandom.current().nextInt(75, 151);
            game.setGold(game.getGold() + goldReward);
            game.setReputation(Math.min(1000, game.getReputation() + 15));
            game.setAlliance(Math.min(1000, game.getAlliance() + 5));
            eventLog.add("AJUDA PRESTADA! Você compartilha 5 rações de comida com o navio em apuros.");
            eventLog.add(String.format("O capitão agradecido lhe oferece %d de ouro como recompensa.", goldReward));
            eventLog.add("Sua reputação e influência entre comerciantes cresce.");
        } else {
            // Só pode oferecer escolta
            int goldReward = ThreadLocalRandom.current().nextInt(30, 61);
            game.setGold(game.getGold() + goldReward);
            game.setReputation(Math.min(1000, game.getReputation() + 5));
            eventLog.add("ESCOLTA OFERECIDA! Sem suprimentos para compartilhar, você oferece escolta até o próximo porto.");
            eventLog.add(String.format("O mercador lhe paga %d de ouro pela proteção.", goldReward));
        }
        
        Port destination = game.getDestinationPort();
        eventLog.add("Com a situação resolvida, você continua sua jornada para " + destination.getName() + ".");
    }
    
    private void processImperialOppression(Game game, List<String> eventLog) {
        eventLog.add("Você testemunha soldados imperiais abusando de civis indefesos...");
        
        // A ação "Intervir" vs "Ignorar" foi escolhida, assumindo intervenção por estar aqui
        eventLog.add("INTERVENÇÃO CORAJOSA! Você decide agir contra a injustiça.");
        
        // Consequências da intervenção
        int crewCombat = game.getShip().getCrew().stream().mapToInt(CrewMember::getCombat).sum();
        boolean success = crewCombat >= 15 || ThreadLocalRandom.current().nextDouble() < 0.7;
        
        if (success) {
            game.setAlliance(Math.min(1000, game.getAlliance() + 25));
            game.setReputation(Math.max(0, game.getReputation() - 10));
            game.setInfamy(Math.min(1000, game.getInfamy() + 5));
            int goldReward = ThreadLocalRandom.current().nextInt(20, 51);
            game.setGold(game.getGold() + goldReward);
            eventLog.add("SUCESSO! Sua intervenção força os soldados a recuar.");
            eventLog.add(String.format("Os civis gratos compartilham %d de ouro, pouco que têm.", goldReward));
            eventLog.add("Sua aliança com os oprimidos cresce, mas você se tornou inimigo do Império.");
        } else {
            int hullDamage = ThreadLocalRandom.current().nextInt(5, 11);
            game.getShip().setHullIntegrity(game.getShip().getHullIntegrity() - hullDamage);
            game.setAlliance(Math.min(1000, game.getAlliance() + 10));
            game.setReputation(Math.max(0, game.getReputation() - 5));
            eventLog.add("RESISTÊNCIA FEROZ! Os soldados contra-atacam seu navio.");
            eventLog.add(String.format("Seu navio sofre %d de dano, mas você salvou vidas inocentes.", hullDamage));
            eventLog.add("Mesmo ferido, você ganhou respeito entre os oprimidos.");
        }
        
        Port destination = game.getDestinationPort();
        eventLog.add("Com a consciência mais leve, você segue para " + destination.getName() + ".");
    }
    
    private void processUndergroundNetwork(Game game, List<String> eventLog) {
        eventLog.add("Você escuta atentamente as informações oferecidas pelo contato clandestino...");
        
        // Informações valiosas da rede revolucionária
        game.setAlliance(Math.min(1000, game.getAlliance() + 15));
        
        // Chance de informações extras baseado em aliança atual
        boolean extraInfo = game.getAlliance() >= 50 || ThreadLocalRandom.current().nextDouble() < 0.4;
        
        if (extraInfo) {
            int goldReward = ThreadLocalRandom.current().nextInt(40, 81);
            game.setGold(game.getGold() + goldReward);
            eventLog.add("INFORMAÇÕES VALIOSAS! O contato revela a localização de um esconderijo imperial.");
            eventLog.add(String.format("Ele lhe entrega %d de ouro como 'fundo de guerra' para a causa.", goldReward));
            eventLog.add("Sua conexão com a rede revolucionária se fortalece significativamente.");
        } else {
            eventLog.add("INFORMAÇÕES BÁSICAS: O contato compartilha movimentos imperiais na região.");
            eventLog.add("Embora úteis, as informações são limitadas - você ainda precisa provar sua lealdade.");
            eventLog.add("Sua influência na rede clandestina cresce lentamente.");
        }
        
        Port destination = game.getDestinationPort();
        eventLog.add("Com novos conhecimentos sobre o Império, você navega em direção a " + destination.getName() + ".");
    }

    /**
     * Aplica bônus de contrato às recompensas quando o encontro está relacionado ao contrato ativo.
     * Isso incentiva os jogadores a aceitar contratos e torna as missões mais rewarding.
     */
    private int applyContractBonus(Game game, SeaEncounterType encounterType, int baseReward, List<String> eventLog) {
        Contract activeContract = game.getActiveContract();
        
        // Se não há contrato ativo ou o encontro não oferece bônus, retorna a recompensa base
        if (activeContract == null || !contractEncounterService.offersContractBonus(encounterType, activeContract)) {
            return baseReward;
        }
        
        // Calcular bônus baseado na facção do contrato (25% de bônus)
        double bonusMultiplier = 1.25;
        int bonusReward = (int) (baseReward * bonusMultiplier);
        int bonusAmount = bonusReward - baseReward;
        
        // Mensagem específica por facção
        String bonusMessage = switch (activeContract.getFaction()) {
            case GUILD -> String.format("💰 Bônus da Guilda: +%d ouro pela cooperação comercial!", bonusAmount);
            case EMPIRE -> String.format("⚔️ Bônus Imperial: +%d ouro por servir o Império!", bonusAmount);
            case BROTHERHOOD -> String.format("🏴‍☠️ Bônus da Irmandade: +%d ouro pela lealdade aos irmãos!", bonusAmount);
            case REVOLUTIONARY -> String.format("🗽 Bônus Revolucionário: +%d ouro pela causa da liberdade!", bonusAmount);
        };
        
        eventLog.add(bonusMessage);
        
        return bonusReward;
    }
    
    /**
     * Aplica custos de viagem: consumo de suprimentos e desgaste do navio.
     * Adiciona tensão econômica e realismo às viagens.
     */
    private List<String> applyTravelCosts(Game game) {
        List<String> costMessages = new ArrayList<>();
        Ship ship = game.getShip();
        
        // Aplicar bônus de navegação do capitão
        double navigationBonus = captainProgressionService.getNavigationBonus(game);
        
        // Constantes de custo de viagem (reduzidas por habilidades de navegação)
        int FOOD_CONSUMPTION = (int) Math.ceil(5 / navigationBonus);  // Navegação reduz consumo
        int RUM_CONSUMPTION = (int) Math.ceil(2 / navigationBonus);   // Navegação reduz consumo
        final int MIN_HULL_DAMAGE = 3;   // Dano mínimo por viagem
        final int MAX_HULL_DAMAGE = 8;   // Dano máximo por viagem (sem encontros)
        
        // 1. Consumo de suprimentos
        if (ship.getFoodRations() >= FOOD_CONSUMPTION) {
            ship.setFoodRations(ship.getFoodRations() - FOOD_CONSUMPTION);
            if (navigationBonus > 1.0) {
                costMessages.add(String.format("⚓ Viagem consome %d unidades de comida. (Navegação eficiente reduz consumo)", FOOD_CONSUMPTION));
            } else {
                costMessages.add(String.format("⚓ Viagem consome %d unidades de comida.", FOOD_CONSUMPTION));
            }
        } else {
            costMessages.add("⚠️ AVISO: Comida insuficiente! A tripulação está passando fome.");
            // Penalidade por falta de comida: reduz moral da tripulação
            ship.getCrew().forEach(crewMember -> {
                int moralPenalty = 10;
                crewMember.setMoral(Math.max(0, crewMember.getMoral() - moralPenalty));
            });
        }
        
        if (ship.getRumRations() >= RUM_CONSUMPTION) {
            ship.setRumRations(ship.getRumRations() - RUM_CONSUMPTION);
            if (navigationBonus > 1.0) {
                costMessages.add(String.format("🍺 Viagem consome %d unidades de rum. (Navegação eficiente reduz consumo)", RUM_CONSUMPTION));
            } else {
                costMessages.add(String.format("🍺 Viagem consome %d unidades de rum.", RUM_CONSUMPTION));
            }
        } else {
            costMessages.add("⚠️ AVISO: Rum insuficiente! A moral da tripulação está baixa.");
            // Penalidade por falta de rum: reduz moral da tripulação
            ship.getCrew().forEach(crewMember -> {
                int moralPenalty = 5;
                crewMember.setMoral(Math.max(0, crewMember.getMoral() - moralPenalty));
            });
        }
        
        // 2. Desgaste natural do navio
        int hullDamage = ThreadLocalRandom.current().nextInt(MIN_HULL_DAMAGE, MAX_HULL_DAMAGE + 1);
        int currentHull = ship.getHullIntegrity();
        int newHull = Math.max(1, currentHull - hullDamage); // Hull nunca fica 0 (navio não afunda)
        ship.setHullIntegrity(newHull);
        
        costMessages.add(String.format("🔧 Desgaste da viagem: -%d pontos de hull (%d → %d).", 
                hullDamage, currentHull, newHull));
        
        // Avisos de estado do hull
        double hullPercentage = (double) newHull / ship.getMaxHullIntegrity();
        if (hullPercentage <= 0.3) {
            costMessages.add("🚨 CASCO CRÍTICO! Procure reparos urgentemente antes da próxima viagem!");
        } else if (hullPercentage <= 0.5) {
            costMessages.add("⚠️ Casco danificado. Considere reparos em breve.");
        }
        
        return costMessages;
    }
    
    /**
     * Encontra o contrato correspondente à escolha inicial do jogador.
     * Busca pelos títulos específicos dos contratos de introdução.
     */
    private Contract findIntroContract(Game game, IntroChoice introChoice) {
        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            return null;
        }
        
        String contractTitle = switch (introChoice) {
            case COOPERATE -> "Transporte de Especiarias";
            case NEUTRAL -> "Contrabando Médico";
            case RESIST -> "Saque ao Esperança Dourada";
        };
        
        return contractRepository.findByStatusAndOriginPortAndRequiredReputationLessThanEqualAndRequiredInfamyLessThanEqualAndRequiredAllianceLessThanEqual(
                ContractStatus.AVAILABLE,
                currentPort,
                game.getReputation(),
                game.getInfamy(),
                game.getAlliance()
        ).stream()
         .filter(contract -> contract.getTitle().equals(contractTitle))
         .findFirst()
         .orElse(null);
    }
}