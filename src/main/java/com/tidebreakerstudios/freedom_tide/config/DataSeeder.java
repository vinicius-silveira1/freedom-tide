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
        try {
            System.out.println("--- Iniciando seeding de dados (v1.23.1 - Economic Rebalance) ---");
            
            if (narrativeEventRepository.findByEventCode("SEEDS_OF_DISCONTENT").isEmpty()) {
                createSeedsOfDiscontentEvent();
            }
            Map<String, Port> ports = createAndGetPorts();
            seedContracts(ports);
            seedUpgrades();
            System.out.println("--- Seeding de dados concluído com sucesso. Rebalanceamento econômico ativo! ---");
        } catch (Exception e) {
            System.err.println("Erro durante seeding de dados: " + e.getMessage());
            e.printStackTrace();
        }
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
                // Portos Originais
                Port.builder().name("Porto Real").type(PortType.IMPERIAL).foodPrice(10).rumPrice(20).toolsPrice(30).shotPrice(40).build(),
                Port.builder().name("Baía da Guilda").type(PortType.GUILD).foodPrice(15).rumPrice(25).toolsPrice(25).shotPrice(45).build(),
                Port.builder().name("Ninho do Corvo").type(PortType.PIRATE).foodPrice(20).rumPrice(15).toolsPrice(35).shotPrice(30).build(),
                
                // Expansão - Região Norte Imperial
                Port.builder().name("Fortaleza de Ferro").type(PortType.IMPERIAL).foodPrice(8).rumPrice(25).toolsPrice(20).shotPrice(35).build(),
                Port.builder().name("Porto da Coroa").type(PortType.IMPERIAL).foodPrice(12).rumPrice(18).toolsPrice(28).shotPrice(42).build(),
                
                // Expansão - Região Leste da Guilda
                Port.builder().name("Entreposto Dourado").type(PortType.GUILD).foodPrice(18).rumPrice(22).toolsPrice(22).shotPrice(48).build(),
                Port.builder().name("Ilha dos Mercadores").type(PortType.GUILD).foodPrice(14).rumPrice(30).toolsPrice(20).shotPrice(50).build(),
                
                // Expansão - Região Sul Independente
                Port.builder().name("Vila dos Pescadores").type(PortType.FREE).foodPrice(8).rumPrice(12).toolsPrice(40).shotPrice(25).build(),
                Port.builder().name("Ruínas de Atlântida").type(PortType.FREE).foodPrice(25).rumPrice(40).toolsPrice(15).shotPrice(60).build(),
                
                // Expansão - Região Oeste Pirata
                Port.builder().name("Refúgio do Kraken").type(PortType.PIRATE).foodPrice(22).rumPrice(10).toolsPrice(45).shotPrice(28).build()
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
                // Contratos Originais
                createGuildContract(ports.get("Baía da Guilda"), ports.get("Porto Real")),
                createRevolutionaryContract(ports.get("Porto Real"), ports.get("Ninho do Corvo")),
                createBrotherhoodContract(ports.get("Ninho do Corvo"), ports.get("Baía da Guilda")),
                
                // Contratos da Expansão - Rotas Imperiais
                createImperialContract(ports.get("Porto da Coroa"), ports.get("Fortaleza de Ferro")),
                createImperialContract(ports.get("Fortaleza de Ferro"), ports.get("Porto Real")),
                
                // Contratos da Expansão - Rotas Comerciais da Guilda
                createGuildContract(ports.get("Entreposto Dourado"), ports.get("Ilha dos Mercadores")),
                createGuildContract(ports.get("Ilha dos Mercadores"), ports.get("Baía da Guilda")),
                
                // Contratos da Expansão - Rotas Independentes
                createHumanitarianContract(ports.get("Vila dos Pescadores"), ports.get("Ruínas de Atlântida")),
                createTreasureContract(ports.get("Ruínas de Atlântida"), ports.get("Vila dos Pescadores")),
                
                // Contratos da Expansão - Rotas Piratas
                createSmugglingContract(ports.get("Refúgio do Kraken"), ports.get("Ninho do Corvo")),
                
                // Contratos Inter-Regionais (Rotas Longas e Lucrativas)
                createLongHaulContract(ports.get("Entreposto Dourado"), ports.get("Fortaleza de Ferro")),
                createDiplomaticContract(ports.get("Porto da Coroa"), ports.get("Vila dos Pescadores")),
                createRiskyContract(ports.get("Ilha dos Mercadores"), ports.get("Refúgio do Kraken"))
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
                .rewardGold(200) // Reduzido de 500 para 200
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
                .rewardGold(150) // Aumentado de 50 para 150
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
                .title("Resgatar ou Vingar")
                .description("Um membro da Irmandade foi capturado pelo Império durante uma operação de resgate. As informações não são precisas: ele pode estar vivo e necessitando de resgate, ou você pode precisar vingar sua morte. Descubra seu destino e aja adequadamente.")
                .faction(Faction.BROTHERHOOD)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(250) // Reduzido de 300 para 250
                .rewardReputation(-30)
                .rewardInfamy(20)
                .rewardAlliance(15)
                .requiredReputation(-20)
                .requiredInfamy(10)
                .requiredAlliance(0)
                .build();
    }

    private Contract createImperialContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Missão da Coroa")
                .description("O Império de Alvor requer transporte urgente de suprimentos militares para uma fortificação distante. A rota é segura e o pagamento garantido pela autoridade imperial.")
                .faction(Faction.EMPIRE)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(180)
                .rewardReputation(30)
                .rewardInfamy(-5)
                .rewardAlliance(-10)
                .requiredReputation(0)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createHumanitarianContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Ajuda Humanitária")
                .description("Uma pequena vila costeira sofreu com tempestades recentes. Leve suprimentos médicos e alimentos para ajudar na recuperação. O pagamento é modesto, mas a gratidão é sincera.")
                .faction(Faction.REVOLUTIONARY) // Alinhado com causas humanitárias
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(120)
                .rewardReputation(0)
                .rewardInfamy(-10)
                .rewardAlliance(40)
                .requiredReputation(0)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createTreasureContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Expedição Arqueológica")
                .description("Ruínas antigas escondem segredos valiosos. Transporte uma equipe de arqueólogos e seus artefatos descobertos. Cuidado com rivais que podem cobiçar os tesouros.")
                .faction(Faction.GUILD) // Interesse comercial em artefatos
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(280)
                .rewardReputation(15)
                .rewardInfamy(5)
                .rewardAlliance(0)
                .requiredReputation(0)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createSmugglingContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Carga Questionável")
                .description("Sem perguntas, sem problemas. Leve esta carga de um porto para outro, evite as patrulhas imperiais, e ganhe bem por sua discrição.")
                .faction(Faction.BROTHERHOOD)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(320)
                .rewardReputation(-20)
                .rewardInfamy(25)
                .rewardAlliance(10)
                .requiredReputation(0)
                .requiredInfamy(15)
                .requiredAlliance(0)
                .build();
    }

    private Contract createLongHaulContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Rota Comercial Longa")
                .description("Uma jornada extensa através de múltiplas regiões. O pagamento compensa o risco e a distância, mas prepare-se para encontros inesperados.")
                .faction(Faction.GUILD)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(450)
                .rewardReputation(20)
                .rewardInfamy(0)
                .rewardAlliance(-5)
                .requiredReputation(10)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createDiplomaticContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Missão Diplomática")
                .description("Transporte documentos diplomáticos importantes entre territórios. A discrição é essencial e a segurança da correspondência é prioritária.")
                .faction(Faction.EMPIRE)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(220)
                .rewardReputation(25)
                .rewardInfamy(-10)
                .rewardAlliance(-15)
                .requiredReputation(5)
                .requiredInfamy(0)
                .requiredAlliance(0)
                .build();
    }

    private Contract createRiskyContract(Port originPort, Port destinationPort) {
        return Contract.builder()
                .title("Alto Risco, Alta Recompensa")
                .description("Uma carga valiosa precisa ser transportada através de águas perigosas. Piratas, tempestades e patrulhas inimigas são esperados. Apenas os mais corajosos se candidatem.")
                .faction(Faction.BROTHERHOOD)
                .status(ContractStatus.AVAILABLE)
                .originPort(originPort)
                .destinationPort(destinationPort)
                .rewardGold(500)
                .rewardReputation(10)
                .rewardInfamy(15)
                .rewardAlliance(5)
                .requiredReputation(0)
                .requiredInfamy(20)
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
        
        // Criar eventos adicionais
        createGhostShipEvent();
        createStormEvent();
        createMerchantEncounterEvent();
        createCourierDilemmaEvent();
        createPirateDiplomacyEvent();
        createTreasureMapEvent();
    }

    private void createGhostShipEvent() {
        EventOption option1 = EventOption.builder()
                .description("Investigar o navio fantasma. Fortune favors the bold.")
                .consequence(EventConsequence.builder()
                        .goldChange(150)
                        .crewMoralChange(-10)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Dar meia volta e fugir da assombração marítima.")
                .consequence(EventConsequence.builder()
                        .crewMoralChange(5)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Tentar comunicar-se com os espíritos do navio.")
                .consequence(EventConsequence.builder()
                        .goldChange(75)
                        .allianceChange(10)
                        .crewMoralChange(-5)
                        .build())
                .build();

        NarrativeEvent ghostEvent = NarrativeEvent.builder()
                .eventCode("GHOST_SHIP_ENCOUNTER")
                .title("O Navio Fantasma")
                .description("Através da névoa matinal surge uma visão perturbadora: um galeão antigo com velas esfarrapadas navega sem rumo. Não há sinais de vida a bordo, mas o navio se move contra o vento. Seus tripulantes sussurram histórias de tesouros perdidos e almas condenadas.")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(ghostEvent);
        option2.setNarrativeEvent(ghostEvent);
        option3.setNarrativeEvent(ghostEvent);

        narrativeEventRepository.save(ghostEvent);
        System.out.println("--- Evento 'O Navio Fantasma' semeado no banco de dados. ---");
    }

    private void createStormEvent() {
        EventOption option1 = EventOption.builder()
                .description("Enfrentar a tempestade de frente. Todos às cordas!")
                .consequence(EventConsequence.builder()
                        .goldChange(-30)
                        .crewMoralChange(10)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Procurar abrigo numa enseada próxima.")
                .consequence(EventConsequence.builder()
                        .goldChange(-10)
                        .crewMoralChange(5)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Jogar parte da carga ao mar para aliviar o navio.")
                .consequence(EventConsequence.builder()
                        .goldChange(-50)
                        .crewMoralChange(-5)
                        .build())
                .build();

        NarrativeEvent stormEvent = NarrativeEvent.builder()
                .eventCode("STORM_ENCOUNTER")
                .title("Tempestade no Horizonte")
                .description("Nuvens escuras se acumulam rapidamente no horizonte. O vento aumenta e as ondas começam a crescer. Sua tripulação olha para você esperando ordens. Uma decisão rápida pode salvar o navio... ou condená-lo.")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(stormEvent);
        option2.setNarrativeEvent(stormEvent);
        option3.setNarrativeEvent(stormEvent);

        narrativeEventRepository.save(stormEvent);
        System.out.println("--- Evento 'Tempestade no Horizonte' semeado no banco de dados. ---");
    }

    private void createMerchantEncounterEvent() {
        EventOption option1 = EventOption.builder()
                .description("Aceitar a troca. Rum por especiarias parece justo.")
                .consequence(EventConsequence.builder()
                        .goldChange(40)
                        .reputationChange(5)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Recusar educadamente e seguir viagem.")
                .consequence(EventConsequence.builder()
                        .crewMoralChange(2)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Tentar roubar o carregamento do mercador.")
                .consequence(EventConsequence.builder()
                        .goldChange(80)
                        .reputationChange(-15)
                        .infamyChange(10)
                        .build())
                .build();

        NarrativeEvent merchantEvent = NarrativeEvent.builder()
                .eventCode("MERCHANT_ENCOUNTER")
                .title("Mercador Navegante")
                .description("Um pequeno navio mercante se aproxima hasteando bandeira de comércio. O capitão propõe uma troca: especiarias exóticas por seu rum extra. Ele parece honesto, mas navios solitários às vezes escondem segredos valiosos.")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(merchantEvent);
        option2.setNarrativeEvent(merchantEvent);
        option3.setNarrativeEvent(merchantEvent);

        narrativeEventRepository.save(merchantEvent);
        System.out.println("--- Evento 'Mercador Navegante' semeado no banco de dados. ---");
    }

    private void createCourierDilemmaEvent() {
        EventOption option1 = EventOption.builder()
                .description("Aceitar entregar a mensagem. Um favor simples.")
                .consequence(EventConsequence.builder()
                        .goldChange(25)
                        .allianceChange(15)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Recusar se envolver em assuntos desconhecidos.")
                .consequence(EventConsequence.builder()
                        .crewMoralChange(3)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Aceitar a mensagem mas lê-la antes de entregar.")
                .consequence(EventConsequence.builder()
                        .goldChange(25)
                        .reputationChange(-5)
                        .allianceChange(5)
                        .build())
                .build();

        NarrativeEvent courierEvent = NarrativeEvent.builder()
                .eventCode("COURIER_DILEMMA")
                .title("O Mensageiro Aflito")
                .description("Um bote se aproxima com um homem desesperado. Ele perdeu seu navio numa tempestade e implora para que você entregue uma mensagem importante para sua família num porto próximo. Seus olhos mostram desespero genuíno, mas mensagens podem carregar mais do que palavras.")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(courierEvent);
        option2.setNarrativeEvent(courierEvent);
        option3.setNarrativeEvent(courierEvent);

        narrativeEventRepository.save(courierEvent);
        System.out.println("--- Evento 'O Mensageiro Aflito' semeado no banco de dados. ---");
    }

    private void createPirateDiplomacyEvent() {
        EventOption option1 = EventOption.builder()
                .description("Aceitar a proposta de parceria. Unidos somos mais fortes.")
                .consequence(EventConsequence.builder()
                        .goldChange(100)
                        .infamyChange(20)
                        .allianceChange(10)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Recusar e preparar-se para possível combate.")
                .consequence(EventConsequence.builder()
                        .reputationChange(10)
                        .crewMoralChange(5)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Fingir aceitar para depois traí-los quando conveniente.")
                .consequence(EventConsequence.builder()
                        .goldChange(150)
                        .reputationChange(-10)
                        .infamyChange(15)
                        .build())
                .build();

        NarrativeEvent diplomacyEvent = NarrativeEvent.builder()
                .eventCode("PIRATE_DIPLOMACY")
                .title("Diplomacia Pirata")
                .description("Um navio pirata se aproxima, mas em vez de atacar, hasteia bandeira de parlamento. O capitão propõe uma aliança temporária para atacar um comboio imperial que passa amanhã. A oferta é tentadora, mas piratas raramente são confiáveis.")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(diplomacyEvent);
        option2.setNarrativeEvent(diplomacyEvent);
        option3.setNarrativeEvent(diplomacyEvent);

        narrativeEventRepository.save(diplomacyEvent);
        System.out.println("--- Evento 'Diplomacia Pirata' semeado no banco de dados. ---");
    }

    private void createTreasureMapEvent() {
        EventOption option1 = EventOption.builder()
                .description("Seguir o mapa imediatamente. X marca o local!")
                .consequence(EventConsequence.builder()
                        .goldChange(200)
                        .crewMoralChange(15)
                        .build())
                .build();

        EventOption option2 = EventOption.builder()
                .description("Vender o mapa para um colecionador no próximo porto.")
                .consequence(EventConsequence.builder()
                        .goldChange(75)
                        .reputationChange(5)
                        .build())
                .build();

        EventOption option3 = EventOption.builder()
                .description("Guardar o mapa para uma ocasião mais segura.")
                .consequence(EventConsequence.builder()
                        .allianceChange(5)
                        .crewMoralChange(-3)
                        .build())
                .build();

        NarrativeEvent treasureEvent = NarrativeEvent.builder()
                .eventCode("TREASURE_MAP_DISCOVERY")
                .title("Mapa do Tesouro")
                .description("Vasculhando os pertences de um navio abandonado, você encontra um mapa do tesouro aparentemente autêntico. A localização é próxima, mas o mapa também pode ser uma armadilha elaborada. Sua tripulação está dividida entre cautela e ganância.")
                .options(Arrays.asList(option1, option2, option3))
                .build();

        option1.setNarrativeEvent(treasureEvent);
        option2.setNarrativeEvent(treasureEvent);
        option3.setNarrativeEvent(treasureEvent);

        narrativeEventRepository.save(treasureEvent);
        System.out.println("--- Evento 'Mapa do Tesouro' semeado no banco de dados. ---");
    }
}