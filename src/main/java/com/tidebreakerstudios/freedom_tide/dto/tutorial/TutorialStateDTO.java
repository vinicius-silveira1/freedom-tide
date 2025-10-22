package com.tidebreakerstudios.freedom_tide.dto.tutorial;

import com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TutorialStateDTO {
    private boolean inTutorial;
    private TutorialPhase currentPhase;
    private TutorialChecklistDTO checklist;
    private List<String> highlightedActions; // e.g., ["TAVERN", "SHIPYARD_REPAIR"]
    private String title;
    private String message;
    private List<String> objectives;
    private List<String> hints; // Dicas contextuais baseadas na escolha inicial
}
