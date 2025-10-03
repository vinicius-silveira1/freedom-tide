package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrewSummaryDTO {
    private int crewCount;
    private int averageMorale;
}
