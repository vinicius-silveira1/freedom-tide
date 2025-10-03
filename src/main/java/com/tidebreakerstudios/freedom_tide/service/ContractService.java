package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.Contract;
import com.tidebreakerstudios.freedom_tide.model.ContractStatus;
import com.tidebreakerstudios.freedom_tide.model.Game;
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
        // 1. Buscar o estado atual do jogo (e do jogador)
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado com o ID: " + gameId));

        // 2. Buscar todos os contratos que estão com o status AVAILABLE
        List<Contract> allAvailableContracts = contractRepository.findByStatus(ContractStatus.AVAILABLE);

        // 3. Filtrar a lista com base nos requisitos (agora à prova de nulos)
        return allAvailableContracts.stream()
                .filter(contract -> contract.getRequiredReputation() == null || game.getReputation() >= contract.getRequiredReputation())
                .filter(contract -> contract.getRequiredInfamy() == null || game.getInfamy() >= contract.getRequiredInfamy())
                .filter(contract -> contract.getRequiredAlliance() == null || game.getAlliance() >= contract.getRequiredAlliance())
                .collect(Collectors.toList());
    }
}