package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa o navio do jogador, sua principal ferramenta e base de operações.
 * A configuração do navio é um reflexo direto da ideologia e das escolhas do jogador.
 */
@Getter
@Setter
@ToString(exclude = {"game", "crew"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ships")
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome do navio, dado pelo jogador.
     */
    private String name;

    /**
     * A classe do navio, que define suas características base.
     */
    @Enumerated(EnumType.STRING)
    private ShipType type;

    /**
     * A integridade do casco (0-100), funcionando como os "pontos de vida" do navio.
     * Se chegar a 0, o navio afunda.
     */
    @Builder.Default
    private Integer hullIntegrity = 100;

    /**
     * Rações de comida para a tripulação. Essencial para manter o moral.
     */
    @Builder.Default
    private Integer foodRations = 100;

    /**
     * Rações de rum, um luxo que pode aumentar o moral ou ser usado em eventos.
     */
    @Builder.Default
    private Integer rumRations = 50;

    /**
     * Peças para reparos no mar, usadas para restaurar a integridade do casco.
     */
    @Builder.Default
    private Integer repairParts = 20;

    /**
     * Munição para os canhões do navio.
     */
    @Builder.Default
    private Integer cannonballs = 40;

    /**
     * A tripulação atualmente a bordo do navio.
     */
    @OneToMany(mappedBy = "ship", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("ship-crew")
    private List<CrewMember> crew = new ArrayList<>();

    /**
     * Melhoria ideológica: permite a criação de propaganda revolucionária.
     */
    @Builder.Default
    private boolean hasPrintingPress = false;

    /**
     * Melhoria ideológica: permite esconder cargas ilegais do Império.
     */
    @Builder.Default
    private boolean hasSmugglingCompartments = false;

    /**
     * Melhoria ideológica: para agradar passageiros da Guilda Mercante.
     */
    @Builder.Default
    private boolean hasLuxuryCabin = false;

    @OneToOne(mappedBy = "ship")
    @JsonBackReference("game-ship")
    private Game game;
}
