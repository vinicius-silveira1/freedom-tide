package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.dto.tutorial.TutorialProgressRequestDTO;
import com.tidebreakerstudios.freedom_tide.dto.tutorial.TutorialStateDTO;

public interface TutorialService {
    TutorialStateDTO getTutorialState(Long gameId);

    TutorialStateDTO progressTutorial(Long gameId, TutorialProgressRequestDTO request);
    
    void completeTutorial(Long gameId);
}
