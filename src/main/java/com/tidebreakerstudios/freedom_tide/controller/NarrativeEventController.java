package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.model.NarrativeEvent;
import com.tidebreakerstudios.freedom_tide.service.NarrativeEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class NarrativeEventController {

    private final NarrativeEventService narrativeEventService;

    /**
     * Endpoint para buscar um evento narrativo aleatório.
     * Simula um encontro aleatório do jogador no mar.
     * @return ResponseEntity com o evento encontrado e status 200 (OK).
     */
    @GetMapping("/random")
    public ResponseEntity<NarrativeEvent> getRandomEvent() {
        NarrativeEvent event = narrativeEventService.getRandomEvent();
        return ResponseEntity.ok(event);
    }
}
