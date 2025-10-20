package com.tidebreakerstudios.freedom_tide.config;

import com.tidebreakerstudios.freedom_tide.model.*;
import com.tidebreakerstudios.freedom_tide.repository.ContractRepository;
import com.tidebreakerstudios.freedom_tide.repository.NarrativeEventRepository;
import com.tidebreakerstudios.freedom_tide.repository.PortRepository;
import com.tidebreakerstudios.freedom_tide.repository.ShipUpgradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final NarrativeEventRepository narrativeEventRepository;
    private final ContractRepository contractRepository;
    private final PortRepository portRepository;
    private final ShipUpgradeRepository shipUpgradeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (narrativeEventRepository.findByEventCode("SEEDS_OF_DISCONTENT").isEmpty()) {
            createSeedsOfDiscontentEvent();
        }
        Map<String, Port> ports = createAndGetPorts();
        seedContracts(ports);
        seedUpgrades();
    }

    private void seedUpgrades() {
        List<ShipUpgrade> upgradeBlueprints = Arrays.asList(
                ShipUpgrade.builder().name("Canhões de Bronze").description("Canhões de qualidade superior que causam mais dano.").type(UpgradeType.CANNONS).modifier(5).cost(1000).portType(PortType.IMPERIAL).build(),
                ShipUpgrade.builder().name("Porão Expandido").description("Uma reorganização inteligente do porão permite carregar mais carga.").type(UpgradeType.CARGO).modifier(50).cost(400).portType(PortType.GUILD).build(),
                ShipUpgrade.builder().name("Bandeira Falsa").description("Uma bandeira do Império ou da Guilda que pode ser usada para enganar navios à distância.").type(UpgradeType.SAILS).modifier(5).cost(1200).portType(PortType.PIRATE).build(),
                ShipUpgrade.builder().name("Casco Reforçado").description("Placas de ferro adicionais reforçam o casco, aumentando sua durabilidade.").type(UpgradeType.HULL).modifier(20).cost(800).build(),
                ShipUpgrade.builder().name("Velas de Linho").description("Velas mais leves e resistentes que melhoram a manobrabilidade.").type(UpgradeType.SAILS).modifier(10).cost(600).build()
        );

        upgradeBlueprints.forEach(blueprint -> {
            shipUpgradeRepository.findByName(blueprint.getName()).ifPresentOrElse(
                    existingUpgrade -> {
                        existingUpgrade.setDescription(blueprint.getDescription());
                        existingUpgrade.setType(blueprint.getType());
                        existingUpgrade.setModifier(blueprint.getModifier());
                        existingUpgrade.setCost(blueprint.getCost());
                        existingUpgrade.setPortType(blueprint.getPortType());
                        shipUpgradeRepository.save(existingUpgrade);
                    },
                    () -> {
                        shipUpgradeRepository.save(blueprint);
                    }
            );
        });
        System.out.println("--- Melhorias de Navio Iniciais sincronizadas (Upsert). ---");
    }

    private Map<String, Port> createAndGetPorts() {
        List<Port> portBlueprints = Arrays.asList(
                Port.builder().name("Porto Real").type(PortType.IMPERIAL).foodPrice(10).rumPrice(20).toolsPrice(30).shotPrice(40).build(),
                Port.builder().name("Baía da Guilda").type(PortType.GUILD).foodPrice(15).rumPrice(25).toolsPrice(25).shotPrice(45).build(),
                Port.builder().name("Ninho do Corvo").type(PortType.PIRATE).foodPrice(20).rumPrice(15).toolsPrice(35).shotPrice(30).build()
        );

        Map<String, Port> savedPorts = portBlueprints.stream()
                .map(blueprint -> portRepository.findByName(blueprint.getName())
                        .map(existingPort -> {
                            existingPort.setType(blueprint.getType());
                            existingPort.setFoodPrice(blueprint.getFoodPrice());
                            existingPort.setRumPrice(blueprint.getRumPrice());
                            existingPort.setToolsPrice(blueprint.getToolsPrice());
                            existingPort.setShotPrice(blueprint.getShotPrice());
                            return portRepository.save(existingPort);
                        })
                        .orElseGet(() -> portRepository.save(blueprint)))
                .collect(Collectors.toMap(Port::getName, Function.identity()));

        System.out.println("--- Portos Iniciais e Preços de Mercado sincronizados (Upsert). ---");
        return savedPorts;
    }

    private void seedContracts(Map<String, Port> ports) {
        List<Contract> contractBlueprints = Arrays.asList(
                createGuildContract(ports.get("Baía da Guilda"), ports.get("Porto Real")),
                createRevolutionaryContract(ports.get("Porto Real"), ports.get("Ninho do Corvo")),
                createBrotherhoodContract(ports.get("Ninho do Corvo"), ports.get("Baía da Guilda"))
        );

        contractBlueprints.forEach(blueprint -> {
            contractRepository.findByTitle(blueprint.getTitle()).ifPresentOrElse(
                existingContract -> {
                    existingContract.setDescription(blueprint.getDescription());
                    existingContract.setFaction(blueprint.getFaction());
                    existingContract.setRewardGold(blueprint.getRewardGold());
                    existingContract.setRewardReputation(blueprint.getRewardReputation());
                    existingContract.setRewardInfamy(blueprint.getRewardInfamy());
                    existingContract.setRewardAlliance(blueprint.getRewardAlliance());
                    existingContract.setRequiredReputation(blueprint.getRequiredReputation());
                    existingContract.setRequiredInfamy(blueprint.getRequiredInfamy());
                    existingContract.setRequiredAlliance(blueprint.getRequiredAlliance());
                    existingContract.setOriginPort(blueprint.getOriginPort());
                    existingContract.setDestinationPort(blueprint.getDestinationPort()); // Add destination
                    existingContract.setStatus(ContractStatus.AVAILABLE);
                    contractRepository.save(existingContract);
                },
                () -> {
                    contractRepository.save(blueprint);
                }
            );
        });
        System.out.println("--- Contratos Iniciais sincronizados (Upsert). ---");
    }

    private Contract createGuildContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Transporte de Manufaturados")
                .description("A Guilda Mercante Unida precisa de um capitão discreto para transportar uma carga de 'bens manufaturados' para uma de suas colônias. O pagamento é generoso e a viagem, espera-se, tranquila.")
                .faction(Faction.GUILD)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(500)
                .rewardReputation(25)
                .rewardInfamy(0)
                .rewardAlliance(-5)
                .requiredReputation(0)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createRevolutionaryContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Interceptar e Libertar")
                .description("Um informante anônimo alega que um navio do Império, com pouca escolta, transporta suprimentos médicos essenciais para uma elite colonial, enquanto a população local sofre. Intercepte a carga e redirecione-a para um porto necessitado.")
                .faction(Faction.REVOLUTIONARY)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(50)
                .rewardReputation(-15)
                .rewardInfamy(10)
                .rewardAlliance(30)
                .requiredReputation(0)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createBrotherhoodContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("O Tributo do Aço")
                .description("A Irmandade de Grani declarou uma rota comercial da Guilda como 'zona de tributo'. Ataque qualquer navio mercante na área e colete o 'tributo' para a Irmandade. A violência é esperada e encorajada.")
                .faction(Faction.BROTHERHOOD)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(300)
                .rewardReputation(-20)
                .rewardInfamy(40)
                .rewardAlliance(0)
                .requiredReputation(0)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private void createSeedsOfDiscontentEvent() {
        EventOption option1 = EventOption.builder()
                .description("Completar a entrega conforme o contrato. Negócios são negócios.")
                .consequence(EventConsequence.builder()
                        .goldChange(200)
                        .reputationChange(15)
                        .crewMoralChange(-5)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Jogar a carga de sementes estéreis ao mar. Ninguém será enganado hoje.")
                .consequence(EventConsequence.builder()
                        .goldChange(-50)
                        .allianceChange(20)
                        .crewMoralChange(10)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Levar as sementes para um Porto Livre para que seus botânicos possam estudá-las.")
                .consequence(EventConsequence.builder()
                        .allianceChange(30)
                        .infamyChange(5)
                        .crewMoralChange(15)
                        .build())
                .build();

        NarrativeEvent seedsEvent = NarrativeEvent.builder()
                .eventCode("SEEDS_OF_DISCONTENT")
                .title("As Sementes da Dívida")
                .description("Você aceitou um contrato da Guilda Mercante para entregar 'sementes melhoradas' a uma colônia distante. No meio da viagem, um tripulante com conhecimento de botânica percebe algo terrível: as sementes são estéreis. É uma tática da Guilda para forçar a colônia a uma dependência perpétua, comprando sementes novas a cada estação. O que você faz?")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(seedsEvent);
        option2.setNarrativeEvent(seedsEvent);
        option3.setNarrativeEvent(seedsEvent);

        narrativeEventRepository.save(seedsEvent);
        System.out.println("--- Evento 'As Sementes da Dívida' semeado no banco de dados. ---");
    }
}