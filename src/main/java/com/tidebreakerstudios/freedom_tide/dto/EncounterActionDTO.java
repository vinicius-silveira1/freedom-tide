package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.EncounterActionType;

public record EncounterActionDTO(
    EncounterActionType actionType,
    String name,
    String description,
    String apiEndpoint
) {}
