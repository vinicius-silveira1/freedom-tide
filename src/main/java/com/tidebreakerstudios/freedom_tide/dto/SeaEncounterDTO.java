package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.SeaEncounterType;

public record SeaEncounterDTO(
    Long id,
    String description,
    SeaEncounterType type
) {}
