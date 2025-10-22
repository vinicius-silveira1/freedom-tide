package com.tidebreakerstudios.freedom_tide.service.tutorial;

import com.tidebreakerstudios.freedom_tide.model.enums.IntroChoice;
import org.springframework.stereotype.Component;
import java.util.List;

import java.util.List;

@Component
public class TutorialContentProvider {

    // Placeholder for tutorial crew members for each path
    public List<String> getTutorialCrewNames(IntroChoice choice) {
        switch (choice) {
            case COOPERATE:
                return List.of("Marinheiro Certificado", "Navegador da Guilda", "Carpinteiro Associado");
            case RESIST:
                return List.of("Pirata Veterano", "Artilheiro Ousado", "Saqueador Ágil");
            case NEUTRAL:
                return List.of("Médico Idealista", "Navegador dos Portos Livres", "Construtor de Barcos");
            default:
                return List.of();
        }
    }

    // Tutorial destinations mapped to actual ports in the game
    public String getTutorialDestination(IntroChoice choice) {
        switch (choice) {
            case COOPERATE:
                return "Baía da Guilda"; // Porto da Guilda para cooperadores
            case RESIST:
                return "Ninho do Corvo"; // Porto pirata para rebeldes
            case NEUTRAL:
                return "Baía da Guilda"; // Porto neutro para idealistas
            default:
                return "Baía da Guilda";
        }
    }

    /**
     * Retorna dicas contextuais personalizadas baseadas na escolha inicial.
     */
    public List<String> getContextualHints(IntroChoice choice, com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase phase) {
        return switch (choice) {
            case COOPERATE -> getCooperateHints(phase);
            case RESIST -> getResistHints(phase);
            case NEUTRAL -> getNeutralHints(phase);
        };
    }

    private List<String> getCooperateHints(com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase phase) {
        return switch (phase) {
            case PREPARATION_CREW -> List.of(
                "A Guilda prefere marinheiros com certificações oficiais.",
                "Tripulantes experientes custam mais, mas são mais confiáveis."
            );
            case PREPARATION_SHIPYARD -> List.of(
                "Um navio bem mantido impressiona os inspetores imperiais.",
                "Reparos completos evitam problemas burocráticos."
            );
            case PREPARATION_MARKET -> List.of(
                "Comerciantes da Guilda oferecem preços fixos e qualidade garantida.",
                "Suprimentos oficiais são aceitos em todos os portos imperiais."
            );
            case JOURNEY_START -> List.of(
                "A Baía da Guilda é um porto seguro para comerciantes imperiais.",
                "Mantenha seus documentos em dia ao viajar por territórios controlados."
            );
            default -> List.of();
        };
    }

    private List<String> getResistHints(com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase phase) {
        return switch (phase) {
            case PREPARATION_CREW -> List.of(
                "Piratas veteranos sabem como sobreviver em águas perigosas.",
                "Tripulação experiente em combate vale cada moeda extra."
            );
            case PREPARATION_SHIPYARD -> List.of(
                "Um navio resistente aguenta mais batalhas.",
                "Nunca se sabe quando você precisará fugir ou lutar."
            );
            case PREPARATION_MARKET -> List.of(
                "No mar, rum mantém a moral alta durante conflitos.",
                "Comida extra é essencial quando não há porto seguro."
            );
            case JOURNEY_START -> List.of(
                "O Ninho do Corvo é território pirata - perfeito para rebeldes.",
                "Capitães destemidos são respeitados em águas sem lei."
            );
            default -> List.of();
        };
    }

    private List<String> getNeutralHints(com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase phase) {
        return switch (phase) {
            case PREPARATION_CREW -> List.of(
                "Idealistas trabalham por causas nobres, não apenas por dinheiro.",
                "Uma tripulação unida por princípios é mais leal."
            );
            case PREPARATION_SHIPYARD -> List.of(
                "Missões humanitárias exigem navios confiáveis.",
                "Vidas dependem da integridade do seu navio."
            );
            case PREPARATION_MARKET -> List.of(
                "Suprimentos extras podem ser compartilhados com necessitados.",
                "Generosidade constrói alianças duradouras."
            );
            case JOURNEY_START -> List.of(
                "A Baía da Guilda acolhe viajantes com boas intenções.",
                "Sua reputação de ajudar outros abre muitas portas."
            );
            default -> List.of();
        };
    }

    /**
     * Retorna mensagens de evento personalizadas para diferentes ações.
     */
    public String getPersonalizedEventMessage(IntroChoice choice, String eventType) {
        return switch (choice) {
            case COOPERATE -> getCooperateEventMessage(eventType);
            case RESIST -> getResistEventMessage(eventType);
            case NEUTRAL -> getNeutralEventMessage(eventType);
        };
    }

    private String getCooperateEventMessage(String eventType) {
        return switch (eventType) {
            case "crew_hired" -> "Novo membro da tripulação registrado oficialmente na Guilda.";
            case "ship_repaired" -> "Reparos certificados pelos engenheiros imperiais.";
            case "supplies_purchased" -> "Suprimentos adquiridos através de canais oficiais.";
            case "contract_available" -> "A Guilda Imperial tem uma missão para capitães confiáveis.";
            default -> "Ação registrada nos arquivos da Guilda.";
        };
    }

    private String getResistEventMessage(String eventType) {
        return switch (eventType) {
            case "crew_hired" -> "Novo corsário se junta à sua causa rebelde.";
            case "ship_repaired" -> "Seu navio está pronto para desafiar o Império.";
            case "supplies_purchased" -> "Suprimentos adquiridos fora dos olhos imperiais.";
            case "contract_available" -> "Há uma oportunidade de atacar as rotas comerciais imperiais.";
            default -> "Mais um passo na luta pela liberdade.";
        };
    }

    private String getNeutralEventMessage(String eventType) {
        return switch (eventType) {
            case "crew_hired" -> "Novo idealista se une à sua missão humanitária.";
            case "ship_repaired" -> "Seu navio está pronto para ajudar quem precisa.";
            case "supplies_purchased" -> "Recursos obtidos para auxiliar os necessitados.";
            case "contract_available" -> "Alguém precisa de ajuda e você pode fazer a diferença.";
            default -> "Cada ação importa para construir um mundo melhor.";
        };
    }
}
