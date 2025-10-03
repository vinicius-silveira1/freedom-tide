package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptainCompassDTO {
    private Integer reputation;
    private Integer infamy;
    private Integer alliance;
}
