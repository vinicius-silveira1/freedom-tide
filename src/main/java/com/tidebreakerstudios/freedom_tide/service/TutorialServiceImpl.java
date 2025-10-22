package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.tutorial.TutorialChecklistDTO;
import com.tidebreakerstudios.freedom_tide.dto.tutorial.TutorialProgressRequestDTO;
import com.tidebreakerstudios.freedom_tide.dto.tutorial.TutorialStateDTO;
import com.tidebreakerstudios.freedom_tide.exception.InvalidTutorialActionException;
import com.tidebreakerstudios.freedom_tide.exception.TutorialNotFoundException;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase;
import com.tidebreakerstudios.freedom_tide.repository.CrewMemberRepository;
import com.tidebreakerstudios.freedom_tide.repository.GameRepository;
import com.tidebreakerstudios.freedom_tide.service.tutorial.TutorialContentProvider;
import com.tidebreakerstudios.freedom_tide.service.tutorial.TutorialMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorialServiceImpl implements TutorialService {

    private final GameRepository gameRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final TutorialContentProvider tutorialContentProvider;
    private final TutorialMetricsService metricsService;

    private static final int REQUIRED_CREW_COUNT = 3;
    private static final int REQUIRED_FOOD_AMOUNT = 50;
    private static final int REQUIRED_RUM_AMOUNT = 25;
    private static final int FULL_HULL_INTEGRITY = 100;

    @Override
    @Transactional(readOnly = true)
    public TutorialStateDTO getTutorialState(Long gameId) {
        Game game = findGameById(gameId);
        if (game.isTutorialCompleted()) {
            return TutorialStateDTO.builder().inTutorial(false).build();
        }
        return buildTutorialState(game);
    }

    @Override
    @Transactional
    public TutorialStateDTO progressTutorial(Long gameId, TutorialProgressRequestDTO request) {
        Game game = findGameById(gameId);
        if (game.isTutorialCompleted()) {
            return getTutorialState(gameId);
        }

        // Registrar ação do jogador
        metricsService.recordPlayerAction(gameId, request.getAction());
        
        // Validar se a ação é válida para a fase atual e avançar apenas se apropriado
        if (isValidActionForCurrentPhase(game, request.getAction())) {
            advancePhase(game, request);
        } else {
            metricsService.recordPlayerStruggles(gameId, "Ação inválida: " + request.getAction());
            throw new InvalidTutorialActionException(
                "Ação '" + request.getAction() + "' não é válida para a fase atual: " + game.getTutorialPhase()
            );
        }

        return getTutorialState(gameId);
    }

    private void advancePhase(Game game, TutorialProgressRequestDTO request) {
        TutorialPhase currentPhase = game.getTutorialPhase();

        switch (currentPhase) {
            case PREPARATION_CREW:
                if (crewMemberRepository.countByShipId(game.getShip().getId()) >= REQUIRED_CREW_COUNT) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.PREPARATION_SHIPYARD);
                    game.setTutorialPhase(TutorialPhase.PREPARATION_SHIPYARD);
                    gameRepository.save(game);
                }
                break;
            case PREPARATION_SHIPYARD:
                if (game.getShip().getHullIntegrity() >= FULL_HULL_INTEGRITY) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.PREPARATION_MARKET);
                    game.setTutorialPhase(TutorialPhase.PREPARATION_MARKET);
                    gameRepository.save(game);
                }
                break;
            case PREPARATION_MARKET:
                if (game.getShip().getFoodRations() >= REQUIRED_FOOD_AMOUNT && game.getShip().getRumRations() >= REQUIRED_RUM_AMOUNT) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.JOURNEY_START);
                    game.setTutorialPhase(TutorialPhase.JOURNEY_START);
                    gameRepository.save(game);
                }
                break;
            case JOURNEY_START:
                // Quando o jogador viaja, avança automaticamente para JOURNEY_EVENT
                if ("START_JOURNEY".equalsIgnoreCase(request.getAction()) || "TRAVEL".equalsIgnoreCase(request.getAction())) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.JOURNEY_EVENT);
                    game.setTutorialPhase(TutorialPhase.JOURNEY_EVENT);
                    gameRepository.save(game);
                }
                break;
            case JOURNEY_EVENT:
                // Tutorial completo apenas quando o jogador chega ao destino (sai do encontro)
                if ("ARRIVE_DESTINATION".equalsIgnoreCase(request.getAction()) || 
                    "ENCOUNTER_RESOLVED".equalsIgnoreCase(request.getAction())) {
                    metricsService.completeTutorial(game.getId());
                    game.setTutorialCompleted(true);
                    game.setTutorialPhase(TutorialPhase.COMPLETED);
                    gameRepository.save(game);
                }
                break;
        }
    }

    private TutorialStateDTO buildTutorialState(Game game) {
        TutorialPhase phase = game.getTutorialPhase();
        TutorialChecklistDTO checklist = buildChecklist(game);

        switch (phase) {
            case PREPARATION_CREW:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("TAVERN"))
                        .title("Primeira Missão: Forme sua Tripulação")
                        .message("Um capitão não é nada sem uma tripulação. Vá para a taverna e contrate seus primeiros marinheiros.")
                        .objectives(List.of("Contratar " + REQUIRED_CREW_COUNT + " tripulantes"))
                        .hints(tutorialContentProvider.getContextualHints(game.getIntroChoice(), phase))
                        .build();
            case PREPARATION_SHIPYARD:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("SHIPYARD"))
                        .title("Primeira Missão: Prepare o Navio")
                        .message("Este navio já viu dias melhores. Leve-o ao estaleiro e repare o casco antes de zarpar.")
                        .objectives(List.of("Reparar o casco do navio para 100%"))
                        .hints(tutorialContentProvider.getContextualHints(game.getIntroChoice(), phase))
                        .build();
            case PREPARATION_MARKET:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("MARKET"))
                        .title("Primeira Missão: Estoque os Suprimentos")
                        .message("Uma tripulação de barriga vazia é uma tripulação amotinada. Compre comida e rum no mercado.")
                        .objectives(List.of("Comprar " + REQUIRED_FOOD_AMOUNT + " de comida", "Comprar " + REQUIRED_RUM_AMOUNT + " de rum"))
                        .hints(tutorialContentProvider.getContextualHints(game.getIntroChoice(), phase))
                        .build();
            case JOURNEY_START:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("TRAVEL"))
                        .title("Primeira Missão: Trace seu Destino")
                        .message("Tudo pronto, Capitão. É hora de zarpar. Selecione seu destino no painel de viagem.")
                        .objectives(List.of("Viajar para " + tutorialContentProvider.getTutorialDestination(game.getIntroChoice())))
                        .hints(tutorialContentProvider.getContextualHints(game.getIntroChoice(), phase))
                        .build();
            case JOURNEY_EVENT:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("ATTACK", "FLEE", "BOARD"))
                        .title("Primeira Missão: Evento no Mar")
                        .message("Você encontrou algo no mar! Use as ações disponíveis para resolver a situação. Esta é sua primeira experiência com encontros marítimos.")
                        .objectives(List.of("Resolver o encontro marítimo usando qualquer ação"))
                        .hints(List.of("Cada ação tem consequências diferentes", "Considere a condição do seu navio", "Sua tripulação influencia o resultado"))
                        .build();
            default:
                return TutorialStateDTO.builder().inTutorial(false).build();
        }
    }

    private TutorialChecklistDTO buildChecklist(Game game) {
        boolean crewHired = crewMemberRepository.countByShipId(game.getShip().getId()) >= REQUIRED_CREW_COUNT;
        boolean shipRepaired = game.getShip().getHullIntegrity() >= FULL_HULL_INTEGRITY;
        boolean suppliesBought = game.getShip().getFoodRations() >= REQUIRED_FOOD_AMOUNT && game.getShip().getRumRations() >= REQUIRED_RUM_AMOUNT;

        return TutorialChecklistDTO.builder()
                .crewHired(crewHired)
                .shipRepaired(shipRepaired)
                .suppliesBought(suppliesBought)
                .build();
    }

    /**
     * Valida se a ação solicitada é apropriada para a fase atual do tutorial.
     */
    private boolean isValidActionForCurrentPhase(Game game, String action) {
        TutorialPhase currentPhase = game.getTutorialPhase();
        
        return switch (currentPhase) {
            case PREPARATION_CREW -> "TAVERN".equalsIgnoreCase(action) || "HIRE_CREW".equalsIgnoreCase(action);
            case PREPARATION_SHIPYARD -> "SHIPYARD".equalsIgnoreCase(action) || "REPAIR_SHIP".equalsIgnoreCase(action);
            case PREPARATION_MARKET -> "MARKET".equalsIgnoreCase(action) || "BUY_SUPPLIES".equalsIgnoreCase(action);
            case JOURNEY_START -> "TRAVEL".equalsIgnoreCase(action) || "START_JOURNEY".equalsIgnoreCase(action);
            case JOURNEY_EVENT -> true; // Qualquer ação é válida durante o evento marítimo
            case COMPLETED -> false; // Tutorial já completo
        };
    }

    /**
     * Completa o tutorial quando o jogador atinge o destino final.
     */
    @Transactional
    public void completeTutorial(Long gameId) {
        Game game = findGameById(gameId);
        if (!game.isTutorialCompleted()) {
            metricsService.recordPhaseTransition(game.getId(), game.getTutorialPhase(), TutorialPhase.COMPLETED);
            game.setTutorialCompleted(true);
            game.setTutorialPhase(TutorialPhase.COMPLETED);
            gameRepository.save(game);
            
            // Finalizar métricas
            metricsService.completeTutorial(gameId);
        }
    }

    private Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new TutorialNotFoundException("Jogo não encontrado com ID: " + gameId));
    }
}