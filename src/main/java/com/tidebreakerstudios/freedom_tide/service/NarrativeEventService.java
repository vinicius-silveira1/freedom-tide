package com.tidebreakerstudios.freedom_tide.service;

import com.tidebreakerstudios.freedom_tide.model.NarrativeEvent;
import com.tidebreakerstudios.freedom_tide.repository.NarrativeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NarrativeEventService {

    private final NarrativeEventRepository narrativeEventRepository;
    private final Random random = new Random();

    /**
     * Busca um evento narrativo aleatório no banco de dados.
     * @return Um NarrativeEvent aleatório.
     * @throws IllegalStateException se não houver eventos no banco de dados.
     */
    public NarrativeEvent getRandomEvent() {
        List<NarrativeEvent> allEvents = narrativeEventRepository.findAll();
        if (allEvents.isEmpty()) {
            throw new IllegalStateException("Nenhum evento narrativo encontrado no banco de dados.");
        }
        return allEvents.get(random.nextInt(allEvents.size()));
    }
}
