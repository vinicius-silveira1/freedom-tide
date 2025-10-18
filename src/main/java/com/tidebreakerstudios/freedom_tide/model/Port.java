package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um porto no mundo do jogo. Portos são hubs para ações do jogador,
 * como encontrar contratos, reparar o navio e interagir com facções.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ports")
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome do porto (ex: "Porto Real", "Baía dos Contrabandistas").
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * O tipo de porto, que determina sua afiliação de facção e as ações disponíveis.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortType type;

    /**
     * O preço de uma unidade de comida neste porto.
     */
    private Integer foodPrice;

    /**
     * O preço de uma unidade de rum neste porto.
     */
    private Integer rumPrice;

    /**
     * O preço de uma unidade de ferramentas neste porto.
     */
    private Integer toolsPrice;

    /**
     * O preço de uma unidade de munição (shot) neste porto.
     */
    private Integer shotPrice;

    /**
     * A lista de contratos que se originam ou estão disponíveis neste porto.
     */
    @OneToMany(mappedBy = "originPort", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("port-contracts")
    @Builder.Default
    private List<Contract> contracts = new ArrayList<>();

}
