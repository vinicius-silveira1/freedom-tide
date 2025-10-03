package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrewAttributesDTO {
    private Integer navigation;
    private Integer artillery;
    private Integer combat;
    private Integer medicine;
    private Integer carpentry;
    private Integer intelligence;
}
