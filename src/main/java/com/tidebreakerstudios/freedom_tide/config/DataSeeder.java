package com.tidebreakerstudios.freedom_tide.config;

import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.repository.ContractRepository;
import com.tidebreakerstudios.freedom_tide.repository.NarrativeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Esta classe é executada na inicialização da aplicação e serve para popular o banco de dados
 * com dados iniciais essenciais (seeding), como os eventos narrativos e contratos.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final NarrativeEventRepository narrativeEventRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (narrativeEventRepository.findByEventCode("SEEDS_OF_DISCONTENT").isEmpty()) {
            createSeedsOfDiscontentEvent();
        }
        if (contractRepository.count() == 0) {
            seedContracts();
        }
    }

    private void seedContracts() {
        List<Contract> contracts = Arrays.asList(
                createGuildContract(),
                createRevolutionaryContract(),
                createBrotherhoodContract()
        );
        contractRepository.saveAll(contracts);
        System.out.println("--- Contratos Iniciais semeados no banco de dados. ---");
    }

    private Contract createGuildContract() {
        return Contract.builder()
                .title("Transporte de Manufaturados")
                .description("A Guilda Mercante Unida precisa de um capitão discreto para transportar uma carga de 'bens manufaturados' para uma de suas colônias. O pagamento é generoso e a viagem, espera-se, tranquila.")
                .faction(Faction.GUILD)
                .status(ContractStatus.AVAILABLE)
                .rewardGold(500)
                .rewardReputation(25)
                .rewardInfamy(0)
                .rewardAlliance(-5) // Trabalhar para a Guilda desagrada os oprimidos
                .build();
    }

    private Contract createRevolutionaryContract() {
        return Contract.builder()
                .title("Interceptar e Libertar")
                .description("Um informante anônimo alega que um navio do Império, com pouca escolta, transporta suprimentos médicos essenciais para uma elite colonial, enquanto a população local sofre. Intercepte a carga e redirecione-a para um porto necessitado.")
                .faction(Faction.REVOLUTIONARY)
                .status(ContractStatus.AVAILABLE)
                .rewardGold(50)
                .rewardReputation(-15)
                .rewardInfamy(10)
                .rewardAlliance(30)
                .build();
    }

    private Contract createBrotherhoodContract() {
        return Contract.builder()
                .title("O Tributo do Aço")
                .description("A Irmandade de Grani declarou uma rota comercial da Guilda como 'zona de tributo'. Ataque qualquer navio mercante na área e colete o 'tributo' para a Irmandade. A violência é esperada e encorajada.")
                .faction(Faction.BROTHERHOOD)
                .status(ContractStatus.AVAILABLE)
                .rewardGold(300)
                .rewardReputation(-20)
                .rewardInfamy(40)
                .rewardAlliance(0)
                .build();
    }

    private void createSeedsOfDiscontentEvent() {
        // Opção 1: A escolha capitalista/leal à Guilda
        EventOption option1 = EventOption.builder()
                .description("Completar a entrega conforme o contrato. Negócios são negócios.")
                .consequence(EventConsequence.builder()
                        .goldChange(200)
                        .reputationChange(15)
                        .crewMoralChange(-5) // Tripulantes com consciência social não gostarão disso
                        .build())
                .build();

        // Opção 2: A escolha puramente rebelde
        EventOption option2 = EventOption.builder()
                .description("Jogar a carga de sementes estéreis ao mar. Ninguém será enganado hoje.")
                .consequence(EventConsequence.builder()
                        .goldChange(-50) // Perda de tempo e combustível
                        .allianceChange(20)
                        .crewMoralChange(10)
                        .build())
                .build();

        // Opção 3: A escolha estratégica e rebelde
        EventOption option3 = EventOption.builder()
                .description("Levar as sementes para um Porto Livre para que seus botânicos possam estudá-las.")
                .consequence(EventConsequence.builder()
                        .allianceChange(30)
                        .infamyChange(5) // A Guilda ouvirá sobre isso
                        .crewMoralChange(15)
                        .build())
                .build();

        // O Evento Narrativo principal
        NarrativeEvent seedsEvent = NarrativeEvent.builder()
                .eventCode("SEEDS_OF_DISCONTENT")
                .title("As Sementes da Dívida")
                .description("Você aceitou um contrato da Guilda Mercante para entregar 'sementes melhoradas' a uma colônia distante. No meio da viagem, um tripulante com conhecimento de botânica percebe algo terrível: as sementes são estéreis. É uma tática da Guilda para forçar a colônia a uma dependência perpétua, comprando sementes novas a cada estação. O que você faz?")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        // Associa as opções ao evento pai
        option1.setNarrativeEvent(seedsEvent);
        option2.setNarrativeEvent(seedsEvent);
        option3.setNarrativeEvent(seedsEvent);

        narrativeEventRepository.save(seedsEvent);
        System.out.println("--- Evento 'As Sementes da Dívida' semeado no banco de dados. ---");
    }
}