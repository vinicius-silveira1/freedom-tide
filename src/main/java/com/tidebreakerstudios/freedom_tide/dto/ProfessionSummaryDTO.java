package com.tidebreakerstudios.freedom_tide.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfessionSummaryDTO {
    private String profession;
    private String professionIcon;
    private String professionColor;
    private int memberCount;
    private double averageRankLevel;
    private int totalXP;
}