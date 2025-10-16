package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa o estado geral de uma sessão de jogo.
 * Esta entidade centraliza a "Bússola do Capitão", que rastreia o progresso
 * e a identidade ideológica do jogador através do mundo.
 */
@Getter
@Setter
@ToString(exclude = "ship")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reputação (0-1000): A posição do jogador dentro do sistema opressor.
     * Aumenta ao servir os interesses do Império e da Guilda.
     */
    @Builder.Default
    private Integer reputation = 0;

    /**
     * Infâmia (0-1000): A reputação do jogador como um predador egoísta.
     * Aumenta com a pirataria e a brutalidade.
     */
    @Builder.Default
    private Integer infamy = 0;

    /**
     * Aliança (0-1000): A reputação do jogador como um símbolo de esperança.
     * Aumenta ao ajudar os oprimidos.
     */
    @Builder.Default
    private Integer alliance = 0;

    /**
     * O navio atualmente comandado pelo jogador nesta sessão de jogo.
     * A relação é OneToOne, pois cada jogo tem apenas um navio principal por vez.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ship_id", referencedColumnName = "id")
    @JsonManagedReference("game-ship")
    private Ship ship;

    @ManyToOne
    @JoinColumn(name = "active_contract_id")
    private Contract activeContract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_port_id")
    private Port currentPort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_port_id")
    private Port destinationPort;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_encounter_id")
    private SeaEncounter currentEncounter;
}
