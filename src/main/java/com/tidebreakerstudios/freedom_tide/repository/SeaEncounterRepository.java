package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.SeaEncounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeaEncounterRepository extends JpaRepository<SeaEncounter, Long> {
}
