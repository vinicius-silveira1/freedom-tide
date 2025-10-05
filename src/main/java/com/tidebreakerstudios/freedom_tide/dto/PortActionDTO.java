package com.tidebreakerstudios.freedom_tide.dto;

import com.tidebreakerstudios.freedom_tide.model.PortActionType;

public record PortActionDTO(
    PortActionType actionType,
    String name,
    String description,
    String apiEndpoint
) {}
