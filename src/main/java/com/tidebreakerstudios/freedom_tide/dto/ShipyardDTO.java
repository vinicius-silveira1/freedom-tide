package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;

@Builder
public record ShipyardDTO(
    String message,
    int repairCost,
    int hullIntegrity,
    int maxHullIntegrity
) {
}
