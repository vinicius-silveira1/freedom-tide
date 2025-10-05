package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameActionResponseDTO {
    private GameStatusResponseDTO gameStatus;
    private List<String> eventLog;
}
