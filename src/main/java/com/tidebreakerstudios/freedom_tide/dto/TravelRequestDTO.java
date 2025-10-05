package com.tidebreakerstudios.freedom_tide.dto;

import jakarta.validation.constraints.NotNull;

public record TravelRequestDTO(
    @NotNull
    Long destinationPortId
) {
}
