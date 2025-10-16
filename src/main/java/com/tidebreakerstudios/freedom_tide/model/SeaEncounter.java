package com.tidebreakerstudios.freedom_tide.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sea_encounters")
public class SeaEncounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeaEncounterType type;

    // Atributos de Combate do Inimigo
    @Builder.Default
    private Integer hull = 100;

    @Builder.Default
    private Integer cannons = 5;

    @Builder.Default
    private Integer sails = 5;

}
