package com.tidebreakerstudios.freedom_tide.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Representa um evento narrativo que o jogador pode encontrar.
 * Cada evento apresenta uma situação e um conjunto de escolhas com consequências.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "narrative_events")
public class NarrativeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Um código único para identificar o evento programaticamente.
     * Ex: "SEEDS_OF_DISCONTENT"
     */
    @Column(unique = true, nullable = false)
    private String eventCode;

    @Column(nullable = false)
    private String title;

    @Lob // Large Object, para textos longos
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * As opções de escolha que o jogador tem neste evento.
     */
    @OneToMany(mappedBy = "narrativeEvent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EventOption> options;
}
