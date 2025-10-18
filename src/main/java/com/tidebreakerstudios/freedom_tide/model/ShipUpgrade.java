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
public class ShipUpgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private UpgradeType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PortType portType; // Facção que vende o upgrade, null para comum

    private int modifier; // O bônus que o upgrade concede
    private int cost;     // O custo em ouro
}
