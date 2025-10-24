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
    private static final int REQUIRED_FOOD_AMOUNT = 35; // Ajustado: jogador começa com 30, precisa comprar 5+
    private static final int REQUIRED_RUM_AMOUNT = 15;  // Ajustado: jogador começa com 12, precisa comprar 3+
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
            // CORREÇÃO ESPECIAL: Se estamos tentando progredir de JOURNEY_EVENT com ARRIVE_DESTINATION
            // mas por algum motivo a validação falhou, forçar a progressão
            if (game.getTutorialPhase() == TutorialPhase.JOURNEY_EVENT && 
                "ARRIVE_DESTINATION".equalsIgnoreCase(request.getAction()) &&
                game.getCurrentPort() != null) { // Confirma que chegou a um porto
                
                metricsService.recordPhaseTransition(game.getId(), TutorialPhase.JOURNEY_EVENT, TutorialPhase.ARRIVAL_ECONOMICS);
                game.setTutorialPhase(TutorialPhase.ARRIVAL_ECONOMICS);
                gameRepository.save(game);
                return getTutorialState(gameId);
            }
            
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
                    game.setTutorialCrewCompleted(true); // Marcar como completado permanentemente
                    gameRepository.save(game);
                }
                break;
            case PREPARATION_SHIPYARD:
                if (game.getShip().getHullIntegrity() >= FULL_HULL_INTEGRITY) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.PREPARATION_MARKET);
                    game.setTutorialPhase(TutorialPhase.PREPARATION_MARKET);
                    game.setTutorialShipCompleted(true); // Marcar como completado permanentemente
                    gameRepository.save(game);
                }
                break;
            case PREPARATION_MARKET:
                if (game.getShip().getFoodRations() >= REQUIRED_FOOD_AMOUNT && game.getShip().getRumRations() >= REQUIRED_RUM_AMOUNT) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.JOURNEY_MECHANICS);
                    game.setTutorialPhase(TutorialPhase.JOURNEY_MECHANICS);
                    game.setTutorialSuppliesCompleted(true); // Marcar como completado permanentemente
                    gameRepository.save(game);
                }
                break;
            case JOURNEY_MECHANICS:
                // Avança automaticamente após jogador confirmar entendimento
                if ("UNDERSTOOD".equalsIgnoreCase(request.getAction()) || "CONTINUE".equalsIgnoreCase(request.getAction())) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.CONTRACT_SYSTEM);
                    game.setTutorialPhase(TutorialPhase.CONTRACT_SYSTEM);
                    gameRepository.save(game);
                }
                // CORREÇÃO: Se o jogador resolve um encontro durante JOURNEY_MECHANICS, pular para ARRIVAL_ECONOMICS
                else if ("ARRIVE_DESTINATION".equalsIgnoreCase(request.getAction()) || 
                         "ENCOUNTER_RESOLVED".equalsIgnoreCase(request.getAction()) ||
                         "ENCOUNTER_ACTION".equalsIgnoreCase(request.getAction())) {
                    System.out.println("DEBUG: CORREÇÃO - Jogador resolveu encontro durante JOURNEY_MECHANICS, pulando para ARRIVAL_ECONOMICS");
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.ARRIVAL_ECONOMICS);
                    game.setTutorialPhase(TutorialPhase.ARRIVAL_ECONOMICS);
                    gameRepository.save(game);
                }
                break;
            case CONTRACT_SYSTEM:
                // Avança após jogador entender contratos e stats
                if ("UNDERSTOOD".equalsIgnoreCase(request.getAction()) || "CONTINUE".equalsIgnoreCase(request.getAction())) {
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
                // Após resolver o encontro, passa para economia de portos
                System.out.println("DEBUG: JOURNEY_EVENT processando ação: " + request.getAction());
                if ("ARRIVE_DESTINATION".equalsIgnoreCase(request.getAction()) || 
                    "ENCOUNTER_RESOLVED".equalsIgnoreCase(request.getAction())) {
                    System.out.println("DEBUG: Progredindo JOURNEY_EVENT -> ARRIVAL_ECONOMICS para game " + game.getId());
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.ARRIVAL_ECONOMICS);
                    game.setTutorialPhase(TutorialPhase.ARRIVAL_ECONOMICS);
                    gameRepository.save(game);
                } else {
                    System.out.println("DEBUG: Ação '" + request.getAction() + "' não reconhecida para progressão");
                }
                break;
            case ARRIVAL_ECONOMICS:
                // Após entender economia, explica upgrades
                if ("UNDERSTOOD".equalsIgnoreCase(request.getAction()) || "CONTINUE".equalsIgnoreCase(request.getAction())) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.ARRIVAL_UPGRADES);
                    game.setTutorialPhase(TutorialPhase.ARRIVAL_UPGRADES);
                    gameRepository.save(game);
                }
                break;
            case ARRIVAL_UPGRADES:
                // Após entender upgrades, vai para graduação final
                if ("UNDERSTOOD".equalsIgnoreCase(request.getAction()) || "CONTINUE".equalsIgnoreCase(request.getAction())) {
                    metricsService.recordPhaseTransition(game.getId(), currentPhase, TutorialPhase.GRADUATION);
                    game.setTutorialPhase(TutorialPhase.GRADUATION);
                    gameRepository.save(game);
                }
                break;
            case GRADUATION:
                // Completa o tutorial após revisão final
                if ("UNDERSTOOD".equalsIgnoreCase(request.getAction()) || "GRADUATE".equalsIgnoreCase(request.getAction())) {
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
                        .message("Uma tripulação de barriga vazia é uma tripulação amotinada. Compre comida e rum no mercado. Lembre-se: cada viagem consome 5 de comida e 2 de rum!")
                        .objectives(List.of("Comprar pelo menos " + REQUIRED_FOOD_AMOUNT + " de comida", "Comprar pelo menos " + REQUIRED_RUM_AMOUNT + " de rum"))
                        .hints(tutorialContentProvider.getContextualHints(game.getIntroChoice(), phase))
                        .build();
            case JOURNEY_MECHANICS:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("CONTINUE"))
                        .title("🎓 Mecânicas de Viagem")
                        .message("IMPORTANTE! Antes de zarpar, entenda como funcionam as viagens:\n\n" +
                                "⚓ Cada viagem consome 5 de comida e 2 de rum\n" +
                                "🔧 Seu navio sofre 3-8 pontos de dano por viagem\n" +
                                "💰 Tripulação recebe salários mensais\n" +
                                "📈 Falta de suprimentos reduz a moral da tripulação\n\n" +
                                "Gerencie seus recursos com sabedoria, Capitão!")
                        .objectives(List.of("Entender as mecânicas de viagem e custos operacionais"))
                        .hints(List.of(
                                "💡 Sempre mantenha suprimentos extras para emergências",
                                "💡 Repare o navio quando o hull estiver abaixo de 50%",
                                "💡 Tripulação com moral alta é mais eficaz em combate"
                        ))
                        .build();
            case CONTRACT_SYSTEM:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("CONTINUE"))
                        .title("📜 Sistema de Contratos e Facções")
                        .message("FUNDAMENTAL! Sua escolha inicial ativou automaticamente um contrato:\n\n" +
                                "📋 CONTRATOS: Missões que fornecem objetivos e recompensas\n" +
                                "🎯 100% dos encontros serão relacionados ao seu contrato ativo\n" +
                                "⚖️ STATS DE FACÇÃO afetam quais contratos você pode aceitar:\n\n" +
                                "👑 REPUTAÇÃO: Acesso a contratos imperiais e da Guilda\n" +
                                "🏴‍☠️ INFÂMIA: Acesso a contratos da Irmandade (piratas)\n" +
                                "🤝 ALIANÇA: Acesso a contratos revolucionários\n\n" +
                                "Suas ações mudam estes stats e abrem novas oportunidades!")
                        .objectives(List.of("Entender como contratos e stats de facção funcionam"))
                        .hints(getContractHints(game))
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
                        .hints(List.of("⚔️ ATAQUE: Mais perigoso, mas maior recompensa", "🏃 FUGA: Mais seguro, evita dano desnecessário", "🏴‍☠️ ABORDAR: Resultado depende da força da sua tripulação"))
                        .build();
            case ARRIVAL_ECONOMICS:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("CONTINUE"))
                        .title("💰 Economia Regional e Comércio")
                        .message("Bem-vindo ao seu destino! Cada porto tem sua economia única:\n\n" +
                                "🏛️ PORTOS IMPERIAIS: Preços moderados, munição cara\n" +
                                "🏪 PORTOS DA GUILDA: Ferramentas baratas, comércio eficiente\n" +
                                "🏴‍☠️ PORTOS PIRATAS: Rum barato, comida cara (contrabando)\n" +
                                "🏘️ PORTOS LIVRES: Extremos - abundância local vs escassez\n\n" +
                                "💡 DICA DE MESTRE: Compare preços antes de comprar!\n" +
                                "Alguns portos vendem por 8 ouro o que outros cobram 25!")
                        .objectives(List.of("Entender as diferenças econômicas entre tipos de porto"))
                        .hints(List.of(
                                "🛒 Sempre confira o mercado local antes de viajar",
                                "📈 Portos especializados têm preços melhores em certas áreas",
                                "🗺️ Planeje rotas comerciais lucrativas"
                        ))
                        .build();
            case ARRIVAL_UPGRADES:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("CONTINUE"))
                        .title("⚙️ Sistema de Melhorias do Navio")
                        .message("Seu navio pode ser melhorado para enfrentar desafios maiores!\n\n" +
                                "🎯 TIPOS DE MELHORIAS DISPONÍVEIS:\n" +
                                "⚔️ Canhões de Bronze: +5 poder de fogo (1.000 ouro)\n" +
                                "📦 Porão Expandido: +50 capacidade de carga (400 ouro)\n" +
                                "🛡️ Casco Reforçado: +20 resistência (800 ouro)\n" +
                                "⛵ Velas de Linho: +10 manobrabilidade (600 ouro)\n" +
                                "🏴 Bandeira Falsa: +5 furtividade (1.200 ouro)\n\n" +
                                "🏪 ONDE ENCONTRAR: Diferentes portos vendem melhorias específicas!")
                        .objectives(List.of("Conhecer o sistema de upgrades e especialidades dos portos"))
                        .hints(List.of(
                                "🏛️ Portos Imperiais: Melhorias militares (canhões)",
                                "🏪 Portos da Guilda: Melhorias comerciais (porão, casco)",
                                "🏴‍☠️ Portos Piratas: Melhorias táticas (bandeira falsa)"
                        ))
                        .build();
            case GRADUATION:
                return TutorialStateDTO.builder()
                        .inTutorial(true)
                        .currentPhase(phase)
                        .checklist(checklist)
                        .highlightedActions(List.of("GRADUATE"))
                        .title("🎓 Graduação: Capitão Certificado!")
                        .message("PARABÉNS! Você completou o treinamento completo!\n\n" +
                                "🎯 RESUMO DO QUE APRENDEU:\n\n" +
                                "👥 Recrutamento e gestão da tripulação\n" +
                                "🔧 Manutenção e reparos do navio\n" +
                                "🛒 Compra inteligente de suprimentos\n" +
                                "⚓ Custos operacionais de viagem\n" +
                                "📜 Sistema de contratos e facções\n" +
                                "⚔️ Estratégias de encontros marítimos\n" +
                                "💰 Economia regional e comércio\n" +
                                "⚙️ Sistema de melhorias do navio\n\n" +
                                "Agora você está pronto para escrever sua própria lenda!\n" +
                                "Os mares da liberdade aguardam, Capitão! ⚓")
                        .objectives(List.of("Revisar todos os conhecimentos adquiridos"))
                        .hints(List.of(
                                "🌟 Use seu contrato ativo para ganhar experiência",
                                "📈 Foque em melhorar stats da facção escolhida",
                                "🚢 Invista em melhorias quando tiver ouro suficiente",
                                "🗺️ Explore diferentes tipos de porto para maximizar lucros"
                        ))
                        .build();
            default:
                return TutorialStateDTO.builder().inTutorial(false).build();
        }
    }

    private TutorialChecklistDTO buildChecklist(Game game) {
        // Usar estado persistente quando já foi completado, caso contrário verificar estado atual
        boolean crewHired = game.isTutorialCrewCompleted() || 
                            crewMemberRepository.countByShipId(game.getShip().getId()) >= REQUIRED_CREW_COUNT;
        boolean shipRepaired = game.isTutorialShipCompleted() || 
                               game.getShip().getHullIntegrity() >= FULL_HULL_INTEGRITY;
        boolean suppliesBought = game.isTutorialSuppliesCompleted() || 
                                 (game.getShip().getFoodRations() >= REQUIRED_FOOD_AMOUNT && 
                                  game.getShip().getRumRations() >= REQUIRED_RUM_AMOUNT);

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
            case JOURNEY_MECHANICS -> "UNDERSTOOD".equalsIgnoreCase(action) || "CONTINUE".equalsIgnoreCase(action) ||
                                     "ARRIVE_DESTINATION".equalsIgnoreCase(action) || "ENCOUNTER_RESOLVED".equalsIgnoreCase(action) ||
                                     "ENCOUNTER_ACTION".equalsIgnoreCase(action); // Aceitar ações de encontro durante explicação
            case CONTRACT_SYSTEM -> "UNDERSTOOD".equalsIgnoreCase(action) || "CONTINUE".equalsIgnoreCase(action);
            case JOURNEY_START -> "TRAVEL".equalsIgnoreCase(action) || "START_JOURNEY".equalsIgnoreCase(action);
            case JOURNEY_EVENT -> true; // Qualquer ação é válida durante o evento marítimo
            case ARRIVAL_ECONOMICS -> "UNDERSTOOD".equalsIgnoreCase(action) || "CONTINUE".equalsIgnoreCase(action);
            case ARRIVAL_UPGRADES -> "UNDERSTOOD".equalsIgnoreCase(action) || "CONTINUE".equalsIgnoreCase(action);
            case GRADUATION -> "UNDERSTOOD".equalsIgnoreCase(action) || "GRADUATE".equalsIgnoreCase(action);
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

    /**
     * Dicas contextuais sobre contratos baseadas na escolha do jogador
     */
    private List<String> getContractHints(Game game) {
        return switch (game.getIntroChoice()) {
            case COOPERATE -> List.of(
                    "👑 Você ganhou REPUTAÇÃO - acesso a contratos imperiais lucrativos!",
                    "🏪 Contratos da Guilda oferecem pagamento estável e seguro",
                    "📈 Alta reputação desbloqueia missões de alto valor"
            );
            case RESIST -> List.of(
                    "🏴‍☠️ Você ganhou INFÂMIA - acesso a contratos de pirataria!",
                    "💰 Contratos da Irmandade são arriscados mas muito lucrativos",
                    "⚔️ Alta infâmia desbloqueia saques e missões perigosas"
            );
            case NEUTRAL -> List.of(
                    "🤝 Você ganhou ALIANÇA - acesso a contratos revolucionários!",
                    "🏥 Contratos humanitários oferecem propósito nobre",
                    "🗽 Alta aliança desbloqueia missões de justiça social"
            );
        };
    }

    private Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new TutorialNotFoundException("Jogo não encontrado com ID: " + gameId));
    }
}