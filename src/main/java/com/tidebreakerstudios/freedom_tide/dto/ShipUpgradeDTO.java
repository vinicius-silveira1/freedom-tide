package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.UpgradeType;

public record ShipUpgradeDTO(
    Long id,
    String name,
    String description,
    UpgradeType type,
    int modifier,
    int cost
) {}
