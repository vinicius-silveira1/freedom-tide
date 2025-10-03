package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa uma única escolha que um jogador pode fazer dentro de um Evento Narrativo.
 * Contém o texto da opção e as consequências diretas dessa escolha.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_options")
public class EventOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * O evento ao qual esta opção pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "narrative_event_id", nullable = false)
    @JsonBackReference("event-option")
    private NarrativeEvent narrativeEvent;

    /**
     * O conjunto de consequências que ocorrem se esta opção for escolhida.
     */
    @Embedded
    private EventConsequence consequence;
}