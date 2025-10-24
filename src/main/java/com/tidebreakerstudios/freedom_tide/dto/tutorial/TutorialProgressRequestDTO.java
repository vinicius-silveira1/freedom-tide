package com.tidebreakerstudios.freedom_tide.dto.tutorial;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TutorialProgressRequestDTO {
    @NotBlank
    private String action;
}
