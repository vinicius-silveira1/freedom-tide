package com.tidebreakerstudios.freedom_tide.dto;

import java.util.List;

public record PortDTO(
    Long id,
    String name,
    String type,
    List<PortActionDTO> availableActions
) {
}
