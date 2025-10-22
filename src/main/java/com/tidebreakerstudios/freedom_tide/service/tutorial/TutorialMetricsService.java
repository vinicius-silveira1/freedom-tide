package com.tidebreakerstudios.freedom_tide.service.tutorial;

import com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase;
import com.tidebreakerstudios.freedom_tide.model.enums.IntroChoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Serviço para coleta de métricas e analytics do sistema de tutorial.
 * Permite rastreamento de progresso e análise de comportamento do jogador.
 */
@Slf4j
@Service
public class TutorialMetricsService {

    private final Map<Long, TutorialSession> activeSessions = new ConcurrentHashMap<>();
    
    /**
     * Registra o início de uma sessão de tutorial.
     */
    public void startTutorialSession(Long gameId, IntroChoice playerChoice) {
        TutorialSession session = new TutorialSession();
        session.gameId = gameId;
        session.startTime = LocalDateTime.now();
        session.playerChoice = playerChoice;
        session.currentPhase = TutorialPhase.PREPARATION_CREW;
        
        activeSessions.put(gameId, session);
        
        log.info("Tutorial iniciado - Game: {} | Escolha: {} | Timestamp: {}", 
                gameId, playerChoice, session.startTime);
    }
    
    /**
     * Registra a transição para uma nova fase do tutorial.
     */
    public void recordPhaseTransition(Long gameId, TutorialPhase fromPhase, TutorialPhase toPhase) {
        TutorialSession session = activeSessions.get(gameId);
        if (session != null) {
            Duration phaseTime = Duration.between(session.phaseStartTime, LocalDateTime.now());
            
            // Log da duração da fase anterior
            log.info("Fase concluída - Game: {} | Fase: {} | Duração: {}s | Escolha: {}", 
                    gameId, fromPhase, phaseTime.getSeconds(), session.playerChoice);
            
            // Atualiza para nova fase
            session.currentPhase = toPhase;
            session.phaseStartTime = LocalDateTime.now();
            session.phasesCompleted++;
        }
    }
    
    /**
     * Registra uma ação do jogador durante o tutorial.
     */
    public void recordPlayerAction(Long gameId, String action) {
        TutorialSession session = activeSessions.get(gameId);
        if (session != null) {
            session.actionsPerformed++;
            
            log.debug("Ação registrada - Game: {} | Ação: {} | Total de ações: {}", 
                    gameId, action, session.actionsPerformed);
        }
    }
    
    /**
     * Registra erro ou dificuldade encontrada pelo jogador.
     */
    public void recordPlayerStruggles(Long gameId, String issue) {
        TutorialSession session = activeSessions.get(gameId);
        if (session != null) {
            session.strugglesEncountered++;
            
            log.warn("Dificuldade detectada - Game: {} | Problema: {} | Total: {}", 
                    gameId, issue, session.strugglesEncountered);
        }
    }
    
    /**
     * Finaliza a sessão de tutorial e calcula métricas finais.
     */
    public void completeTutorial(Long gameId) {
        TutorialSession session = activeSessions.remove(gameId);
        if (session != null) {
            Duration totalTime = Duration.between(session.startTime, LocalDateTime.now());
            
            log.info("Tutorial concluído - Game: {} | Tempo total: {}min | Ações: {} | Dificuldades: {} | Escolha: {}", 
                    gameId, 
                    totalTime.toMinutes(), 
                    session.actionsPerformed, 
                    session.strugglesEncountered,
                    session.playerChoice);
        }
    }
    
    /**
     * Obtém estatísticas da sessão atual (para debugging ou admin).
     */
    public String getSessionStats(Long gameId) {
        TutorialSession session = activeSessions.get(gameId);
        if (session == null) {
            return "Sessão não encontrada";
        }
        
        Duration elapsed = Duration.between(session.startTime, LocalDateTime.now());
        return String.format(
            "Sessão %d: %s | Fase: %s | Tempo: %dmin | Ações: %d", 
            gameId, 
            session.playerChoice, 
            session.currentPhase, 
            elapsed.toMinutes(), 
            session.actionsPerformed
        );
    }
    
    /**
     * Classe interna para armazenar dados da sessão de tutorial.
     */
    private static class TutorialSession {
        Long gameId;
        LocalDateTime startTime;
        LocalDateTime phaseStartTime;
        IntroChoice playerChoice;
        TutorialPhase currentPhase;
        int phasesCompleted = 0;
        int actionsPerformed = 0;
        int strugglesEncountered = 0;
        
        public TutorialSession() {
            this.phaseStartTime = LocalDateTime.now();
        }
    }
}