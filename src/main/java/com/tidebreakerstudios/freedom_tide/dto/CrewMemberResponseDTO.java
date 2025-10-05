package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrewMemberResponseDTO {
    private Long id;
    private String name;
    private String personality;
    private Integer moral;
    private Integer loyalty;
    private Integer salary;
    private CrewAttributesDTO attributes;
}
