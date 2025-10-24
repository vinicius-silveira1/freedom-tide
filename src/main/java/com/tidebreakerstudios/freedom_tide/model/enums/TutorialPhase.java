package com.tidebreakerstudios.freedom_tide.model.enums;

public enum TutorialPhase {
    PREPARATION_CREW,       // Player must hire crew
    PREPARATION_SHIPYARD,   // Player must repair the ship
    PREPARATION_MARKET,     // Player must buy supplies
    JOURNEY_MECHANICS,      // Explain travel costs and mechanics
    CONTRACT_SYSTEM,        // NEW: Explain contracts and faction stats
    JOURNEY_START,          // Player must select the tutorial destination
    JOURNEY_EVENT,          // Player is in the middle of a scripted sea event
    ARRIVAL_ECONOMICS,      // NEW: Explain port economics and trading
    ARRIVAL_UPGRADES,       // NEW: Explain ship upgrades system
    GRADUATION,             // NEW: Final comprehensive review
    COMPLETED               // Tutorial is finished
}
