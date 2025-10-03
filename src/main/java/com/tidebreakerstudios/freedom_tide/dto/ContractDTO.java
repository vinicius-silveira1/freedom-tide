package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.Faction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractDTO {
    private Long id;
    private String title;
    private String description;
    private Faction faction;
    private int rewardGold;
    private int rewardReputation;
    private int rewardInfamy;
    private int rewardAlliance;

    // Requirements
    private Integer requiredReputation;
    private Integer requiredInfamy;
    private Integer requiredAlliance;
}
