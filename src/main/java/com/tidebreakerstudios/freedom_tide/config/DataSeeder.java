package com.tidebreakerstudios.freedom_tide.config;

import com.tidebreakerstudios.freedom_tide.model.EventConsequence;
import com.tidebreakerstudios.freedom_tide.model.EventOption;
import com.tidebreakerstudios.freedom_tide.model.NarrativeEvent;
import com.tidebreakerstudios.freedom_tide.repository.NarrativeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Esta classe é executada na inicialização da aplicação e serve para popular o banco de dados
 * com dados iniciais essenciais (seeding), como os eventos narrativos.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final NarrativeEventRepository narrativeEventRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verifica se o evento já existe para não criar duplicatas a cada reinício
        if (narrativeEventRepository.findByEventCode("SEEDS_OF_DISCONTENT").isEmpty()) {
            createSeedsOfDiscontentEvent();
        }
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