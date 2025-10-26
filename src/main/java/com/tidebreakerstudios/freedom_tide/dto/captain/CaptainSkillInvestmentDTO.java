package com.tidebreakerstudios.freedom_tide.dto.captain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de investimento em habilidades do capitão.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptainSkillInvestmentDTO {
    
    private String skillName;
}