package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa um membro individual da tripulação do jogador.
 * Cada membro tem seus próprios atributos, personalidade e estado moral,
 * refletindo a face humana da desigualdade no mundo do jogo.
 */
@Getter
@Setter
@ToString(exclude = "ship")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crew_members")
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome do membro da tripulação.
     */
    private String name;

    // --- Atributos (1-10) ---
    private Integer navigation;
    private Integer artillery;
    private Integer combat;
    private Integer medicine;
    private Integer carpentry;
    private Integer intelligence;

    /**
     * Nível de Desespero no momento do recrutamento.
     * Um valor alto significa salário mais baixo, mas maior risco de problemas.
     */
    private Integer despairLevel;

    /**
     * A personalidade ideológica do tripulante, que afeta seu moral
     * com base nas ações do capitão.
     */
    @Enumerated(EnumType.STRING)
    private CrewPersonality personality;

    /**
     * O moral individual do tripulante (0-100).
     * Contribui para o moral geral da tripulação.
     */
    @Builder.Default
    private Integer moral = 70;

    /**
    * A lealdade do tripulante para com o capitão (0-100).
    * Pondera a influência do seu moral no cálculo do moral geral.
    */
    @Builder.Default
    private Integer loyalty = 50;

    /**
     * O navio ao qual este membro da tripulação pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id")
    @JsonBackReference("ship-crew")
    private Ship ship;
}
