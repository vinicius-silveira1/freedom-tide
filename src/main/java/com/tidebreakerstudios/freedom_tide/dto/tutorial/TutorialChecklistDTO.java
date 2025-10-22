package com.tidebreakerstudios.freedom_tide.dto.tutorial;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TutorialChecklistDTO {
    private boolean crewHired;
    private boolean shipRepaired;
    private boolean suppliesBought;
}
