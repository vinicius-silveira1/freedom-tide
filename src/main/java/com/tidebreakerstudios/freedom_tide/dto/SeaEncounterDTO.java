package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.SeaEncounterType;

import java.util.List;

public record SeaEncounterDTO(
    Long id,
    String description,
    SeaEncounterType type,
    List<EncounterActionDTO> availableActions
) {}
