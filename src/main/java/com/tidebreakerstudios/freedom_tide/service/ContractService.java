package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.Contract;
import com.tidebreakerstudios.freedom_tide.model.ContractStatus;
import com.tidebreakerstudios.freedom_tide.model.Game;
import com.tidebreakerstudios.freedom_tide.model.Port;
import com.tidebreakerstudios.freedom_tide.repository.ContractRepository;
import com.tidebreakerstudios.freedom_tide.repository.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final GameRepository gameRepository; // Injetado para buscar o estado do jogo

    /**
     * Busca todos os contratos disponíveis e os filtra com base na Bússola do Capitão do jogador.
     * @param gameId O ID do jogo atual.
     * @return Uma lista de contratos que o jogador tem permissão para ver e aceitar.
     */
    public List<Contract> getAvailableContractsForGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + gameId));

        Port currentPort = game.getCurrentPort();
        if (currentPort == null) {
            return List.of(); // Se não está em um porto, não há contratos.
        }

        return contractRepository.findByStatusAndOriginPortAndRequiredReputationLessThanEqualAndRequiredInfamyLessThanEqualAndRequiredAllianceLessThanEqual(
                ContractStatus.AVAILABLE,
                currentPort,
                game.getReputation(),
                game.getInfamy(),
                game.getAlliance()
        );
    }
}