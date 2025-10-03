package com.tidebreakerstudios.freedom_tide.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1024)
    private String description;

    @Enumerated(EnumType.STRING)
    private Faction faction;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    // Rewards
    private int rewardGold;
    private int rewardReputation;
    private int rewardInfamy;
    private int rewardAlliance;

    // Requirements
    @Builder.Default
    private Integer requiredReputation = 0;
    @Builder.Default
    private Integer requiredInfamy = 0;
    @Builder.Default
    private Integer requiredAlliance = 0;
}
