package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameStatusResponseDTO {
    private Long id;
    private CaptainCompassDTO captainCompass;
    private ShipSummaryDTO ship;
    private CrewSummaryDTO crew;
    private ContractDTO activeContract;
    private PortDTO currentPort;
    private SeaEncounterDTO currentEncounter;
    private boolean gameOver;
    private String gameOverReason;
}
