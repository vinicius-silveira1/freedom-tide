package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_port_id")
    @JsonBackReference("port-contracts")
    private Port originPort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_port_id")
    private Port destinationPort;

    @OneToOne(mappedBy = "activeContract")
    @JsonBackReference("game-contract")
    private Game game;
}
