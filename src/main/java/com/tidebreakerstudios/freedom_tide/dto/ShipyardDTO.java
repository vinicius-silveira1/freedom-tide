package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ShipyardDTO(
    String message,
    int repairCost,
    int hullIntegrity,
    int maxHullIntegrity,
    List<ShipUpgradeDTO> availableUpgrades
) {
}
