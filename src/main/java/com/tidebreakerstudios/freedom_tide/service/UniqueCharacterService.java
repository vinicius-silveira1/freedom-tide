package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.RecruitCrewMemberRequest;
import com.tidebreakerstudios.freedom_tide.model.CrewPersonality;
import com.tidebreakerstudios.freedom_tide.model.PortType;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewProfession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Serviço responsável por gerar personagens únicos e interessantes 
 * para contratação na taverna, cada um com background, personalidade 
 * e história própria.
 */
@Service
@RequiredArgsConstructor
public class UniqueCharacterService {

    // === PERSONAGENS POR PORTO IMPERIAL ===
    private final List<CharacterTemplate> imperialCharacters = List.of(
        new CharacterTemplate(
            "\"Capitã Tempestade\" Elena Biscaia", 
            "Ex-capitã da Marinha Imperial, perdeu sua patente por se recusar a bombardear um porto civil.",
            "\"Conheço cada corrente destes mares... e cada injustiça também.\"",
            CrewPersonality.HONEST, 3, 9, 3, 4, 2, 1, 7
        ),
        new CharacterTemplate(
            "\"Almirante Caído\" Augusto Nobre",
            "Oficial naval de alta patente, exilado após questionar ordens cruéis.",
            "\"A honra verdadeira não se curva diante da tirania.\"",
            CrewPersonality.HONEST, 4, 8, 2, 5, 1, 2, 9
        ),
        new CharacterTemplate(
            "\"Doutora Imperial\" Victoria Salvadora",
            "Médica da corte que fugiu após ser forçada a negar tratamento aos pobres.",
            "\"A medicina não conhece classe social... apenas sofrimento.\"",
            CrewPersonality.HONEST, 2, 2, 1, 2, 9, 1, 8
        ),
        new CharacterTemplate(
            "\"Engenheiro Real\" Leonardo Estrutura",
            "Arquiteto naval imperial, desiludido com navios de guerra.",
            "\"Construí máquinas de guerra... agora quero construir esperança.\"",
            CrewPersonality.HONEST, 5, 3, 2, 3, 2, 9, 7
        ),
        new CharacterTemplate(
            "\"Conselheiro Banido\" Maestro Tacticus",
            "Ex-estrategista da corte, exilado por propor paz em vez de guerra.",
            "\"A verdadeira vitória é quando ninguém precisa lutar.\"",
            CrewPersonality.HONEST, 2, 2, 1, 1, 3, 1, 10
        )
    );

    // === PERSONAGENS POR PORTO DA GUILDA ===
    private final List<CharacterTemplate> guildCharacters = List.of(
        new CharacterTemplate(
            "\"Mãos de Ouro\" Esperança Metalúrgica",
            "Inventora de munições especiais, fugiu da Guilda após descobrir seus planos bélicos.",
            "\"Transformo pólvora em justiça, uma explosão por vez.\"",
            CrewPersonality.REBEL, 4, 1, 8, 3, 2, 6, 9
        ),
        new CharacterTemplate(
            "\"Raposa Dourada\" Merchant Goldstein",
            "Comerciante que perdeu tudo em esquema corrupto da Guilda.",
            "\"O dinheiro é poder... e eu pretendo recuperar o meu.\"",
            CrewPersonality.GREEDY, 5, 4, 1, 1, 2, 2, 9
        ),
        new CharacterTemplate(
            "\"Rosa dos Ventos\" Isabela Navegante",
            "Filha de comerciante arruinado, sonha restaurar a honra da família.",
            "\"Cada milha navegada me aproxima da redenção... e da fortuna.\"",
            CrewPersonality.GREEDY, 5, 7, 2, 2, 1, 2, 8
        ),
        new CharacterTemplate(
            "\"Mestre das Obras\" Vicente Arquiteto",
            "Engenheiro naval expulso da Guilda por denunciar corrupção.",
            "\"A verdadeira engenharia constrói mais que navios... constrói esperança.\"",
            CrewPersonality.REBEL, 5, 3, 2, 2, 4, 9, 8
        ),
        new CharacterTemplate(
            "\"Contador Rebelde\" Mateus Cifras",
            "Ex-contador da Guilda que descobriu esquemas de lavagem de dinheiro.",
            "\"Os números não mentem... mas os homens sim.\"",
            CrewPersonality.REBEL, 6, 2, 1, 2, 1, 3, 9
        ),
        new CharacterTemplate(
            "\"Baronesa Falida\" Catarina dos Cofres",
            "Ex-nobre que perdeu fortuna em jogos manipulados da Guilda.",
            "\"Aprendi que ouro compra tudo... exceto dignidade.\"",
            CrewPersonality.GREEDY, 7, 6, 2, 3, 2, 4, 8
        )
    );

    // === PERSONAGENS POR PORTO PIRATA ===
    private final List<CharacterTemplate> pirateCharacters = List.of(
        new CharacterTemplate(
            "\"Lâmina Sangrenta\" Diego Cortante",
            "Gladiador de arena, comprou liberdade com sangue de adversários.",
            "\"A arena me fez... agora busco oponentes dignos no mar.\"",
            CrewPersonality.BLOODTHIRSTY, 7, 1, 2, 9, 2, 1, 3
        ),
        new CharacterTemplate(
            "\"Ferro Frio\" Marcus Tronada",
            "Ex-soldado imperial, desertor após presenciar massacre de civis.",
            "\"Meus canhões falam a linguagem que todos entendem.\"",
            CrewPersonality.BLOODTHIRSTY, 6, 2, 9, 6, 1, 3, 4
        ),
        new CharacterTemplate(
            "\"Trovão Negro\" Rodrigo Bombardeiro",
            "Artilheiro pirata aposentado, busca última grande aventura.",
            "\"Cada tiro é uma obra de arte... mortal.\"",
            CrewPersonality.BLOODTHIRSTY, 7, 3, 9, 4, 1, 2, 5
        ),
        new CharacterTemplate(
            "\"Duende das Sombras\" Catarina Sigilosa",
            "Ex-assassina da Guilda, traída e abandonada em missão.",
            "\"Ensinarei meus antigos mestres o verdadeiro significado de traição.\"",
            CrewPersonality.REBEL, 5, 3, 1, 8, 3, 2, 7
        ),
        new CharacterTemplate(
            "\"Capitão Fantasma\" Barnabé Sombrio",
            "Pirata lendário que fingiu a própria morte para escapar da forca.",
            "\"A morte me rejeitou... agora sou problema dos vivos.\"",
            CrewPersonality.BLOODTHIRSTY, 8, 7, 3, 8, 1, 4, 6
        ),
        new CharacterTemplate(
            "\"Sereia Negra\" Marina Abissal",
            "Ex-contrabandista que conhece cada rota secreta dos mares.",
            "\"As sombras são minha casa... e o mar, meu quintal.\"",
            CrewPersonality.REBEL, 6, 8, 2, 5, 2, 3, 7
        ),
        new CharacterTemplate(
            "\"Punhal Envenenado\" Vítor Peçonha",
            "Assassino especializado em eliminar alvos em alto mar.",
            "\"O veneno age devagar... como a vingança.\"",
            CrewPersonality.BLOODTHIRSTY, 9, 1, 4, 8, 1, 2, 4
        )
    );

    // === PERSONAGENS POR PORTO LIVRE ===
    private final List<CharacterTemplate> freeCharacters = List.of(
        new CharacterTemplate(
            "\"São Curador\" Padre Bonifácio",
            "Padre que perdeu fé após epidemia devastar sua paróquia.",
            "\"Se Deus não salva os inocentes... eu salvarei.\"",
            CrewPersonality.HONEST, 9, 1, 1, 2, 9, 2, 6
        ),
        new CharacterTemplate(
            "\"Doutora Contraveneno\" Lydia Herbalista",
            "Médica que desenvolveu curas para doenças 'incuráveis' dos pobres.",
            "\"A medicina não deveria ser privilégio dos ricos.\"",
            CrewPersonality.REBEL, 3, 2, 1, 1, 8, 3, 9
        ),
        new CharacterTemplate(
            "\"Anjo dos Mares\" Sofia Curadora",
            "Enfermeira naval, desertou após ver tripulantes sendo sacrificados.",
            "\"Cada vida que salvo é uma vitória contra a crueldade.\"",
            CrewPersonality.HONEST, 2, 1, 2, 3, 9, 1, 7
        ),
        new CharacterTemplate(
            "\"Velho Saltério\" Tomás Marés",
            "40 anos navegando, perdeu família em naufrágio causado por negligência da Guilda.",
            "\"Estes ventos me sussurram segredos... alguns muito amargos.\"",
            CrewPersonality.REBEL, 8, 8, 1, 2, 1, 5, 6
        ),
        new CharacterTemplate(
            "\"Sereia da Madeira\" Ana Entalhadora",
            "Artesã que esculpe mastros com figuras protetoras.",
            "\"Minhas esculturas guardam os navios... e as almas que navegam.\"",
            CrewPersonality.HONEST, 2, 2, 1, 1, 3, 8, 7
        ),
        new CharacterTemplate(
            "\"Cérebro de Mercúrio\" Professor Ítalo",
            "Ex-conselheiro imperial, banido por propor reformas sociais.",
            "\"A revolução começa na mente... depois conquista os mares.\"",
            CrewPersonality.REBEL, 4, 3, 1, 2, 3, 2, 10
        ),
        new CharacterTemplate(
            "\"Punho de Ferro\" Benedito Guerreiro",
            "Protetor de caravanas, perdeu emprego para corsários corruptos.",
            "\"A honra não se compra... mas se defende com sangue.\"",
            CrewPersonality.HONEST, 4, 2, 3, 8, 4, 3, 5
        ),
        new CharacterTemplate(
            "\"Estrela do Norte\" Joaquim Bússola",
            "Cartógrafo imperial que descobriu rotas secretas da corrupção.",
            "\"Os mapas revelam mais que geografia... revelam a verdade.\"",
            CrewPersonality.REBEL, 6, 8, 1, 1, 2, 3, 9
        )
    );



    /**
     * Gera um personagem único baseado no tipo de porto.
     */
    public RecruitCrewMemberRequest generateUniqueCharacter(PortType portType) {
        List<CharacterTemplate> availableTemplates = getTemplatesForPort(portType);
        
        if (availableTemplates.isEmpty()) {
            // Fallback para geração aleatória se não houver templates
            return generateRandomCharacter(portType);
        }

        CharacterTemplate template = availableTemplates.get(
            ThreadLocalRandom.current().nextInt(availableTemplates.size())
        );

        return createRequestFromTemplate(template);
    }

    /**
     * Gera múltiplos personagens únicos para uma taverna específica por tipo de porto.
     */
    public List<RecruitCrewMemberRequest> generateTavernCharacters(PortType portType, int count) {
        List<RecruitCrewMemberRequest> characters = new ArrayList<>();
        List<CharacterTemplate> availableTemplates = getTemplatesForPort(portType);
        List<CharacterTemplate> usedTemplates = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Filtrar templates já utilizados
            List<CharacterTemplate> unusedTemplates = availableTemplates.stream()
                .filter(template -> !usedTemplates.contains(template))
                .toList();
            
            CharacterTemplate selectedTemplate;
            if (!unusedTemplates.isEmpty()) {
                // Escolher template não utilizado
                selectedTemplate = unusedTemplates.get(
                    ThreadLocalRandom.current().nextInt(unusedTemplates.size())
                );
            } else {
                // Se todos já foram usados, escolher aleatório (pode acontecer em portos com poucos personagens)
                selectedTemplate = availableTemplates.get(
                    ThreadLocalRandom.current().nextInt(availableTemplates.size())
                );
            }
            
            usedTemplates.add(selectedTemplate);
            characters.add(createRequestFromTemplate(selectedTemplate));
        }
        
        return characters;
    }

    // === MÉTODOS PRIVADOS ===

    private List<CharacterTemplate> getTemplatesForPort(PortType portType) {
        return switch (portType) {
            case IMPERIAL -> imperialCharacters;
            case GUILD -> guildCharacters;
            case PIRATE -> pirateCharacters;
            case FREE -> freeCharacters;
            default -> freeCharacters; // fallback
        };
    }



    private RecruitCrewMemberRequest createRequestFromTemplate(CharacterTemplate template) {
        RecruitCrewMemberRequest request = new RecruitCrewMemberRequest();
        request.setName(template.name);
        request.setBackground(template.background);
        request.setCatchphrase(template.catchphrase);
        request.setPersonality(template.personality);
        request.setDespairLevel(template.despairLevel);
        request.setNavigation(template.navigation);
        request.setArtillery(template.artillery);
        request.setCombat(template.combat);
        request.setMedicine(template.medicine);
        request.setCarpentry(template.carpentry);
        request.setIntelligence(template.intelligence);
        return request;
    }

    private RecruitCrewMemberRequest generateRandomCharacter(PortType portType) {
        // Fallback para geração aleatória quando não há templates
        RecruitCrewMemberRequest request = new RecruitCrewMemberRequest();
        request.setName("Marinheiro Comum");
        request.setPersonality(CrewPersonality.HONEST);
        request.setDespairLevel(ThreadLocalRandom.current().nextInt(1, 11));
        request.setNavigation(ThreadLocalRandom.current().nextInt(1, 6));
        request.setArtillery(ThreadLocalRandom.current().nextInt(1, 6));
        request.setCombat(ThreadLocalRandom.current().nextInt(1, 6));
        request.setMedicine(ThreadLocalRandom.current().nextInt(1, 6));
        request.setCarpentry(ThreadLocalRandom.current().nextInt(1, 6));
        request.setIntelligence(ThreadLocalRandom.current().nextInt(1, 6));
        return request;
    }

    // === CLASSE INTERNA PARA TEMPLATES ===
    private record CharacterTemplate(
        String name,
        String background,
        String catchphrase,
        CrewPersonality personality,
        int despairLevel,
        int navigation,
        int artillery,
        int combat,
        int medicine,
        int carpentry,
        int intelligence
    ) {}
}