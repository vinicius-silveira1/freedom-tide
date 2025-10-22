package com.tidebreakerstudios.freedom_tide.model.enums;

public enum TutorialPhase {
    PREPARATION_CREW,       // Player must hire crew
    PREPARATION_SHIPYARD,   // Player must repair the ship
    PREPARATION_MARKET,     // Player must buy supplies
    JOURNEY_START,          // Player must select the tutorial destination
    JOURNEY_EVENT,          // Player is in the middle of a scripted sea event
    COMPLETED               // Tutorial is finished
}
