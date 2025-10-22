package com.tidebreakerstudios.freedom_tide.dto.tutorial;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TutorialProgressRequestDTO {
    @NotBlank
    private String action;
}
