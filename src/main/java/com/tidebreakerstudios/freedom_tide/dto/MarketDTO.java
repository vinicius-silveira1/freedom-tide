package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketDTO {
    private Integer foodPrice;
    private Integer rumPrice;
    private Integer toolsPrice;
    private Integer shotPrice;

    private Integer shipFood;
    private Integer shipRum;
    private Integer shipTools;
    private Integer shipShot;
    private Integer shipGold;
}
