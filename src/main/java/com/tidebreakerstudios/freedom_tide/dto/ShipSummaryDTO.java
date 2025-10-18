package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShipSummaryDTO {
    private String name;
    private String type;
    private Integer hullIntegrity;
    private Integer gold;
    private Integer foodRations;
    private Integer rumRations;
    private Integer repairParts;
    private Integer cannonballs;
    private List<ShipUpgradeDTO> upgrades;
}
