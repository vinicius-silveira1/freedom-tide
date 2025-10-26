package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tidebreakerstudios.freedom_tide.model.enums.IntroChoice;
import com.tidebreakerstudios.freedom_tide.model.enums.TutorialPhase;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa o estado geral de uma sessão de jogo.
 * Esta entidade centraliza a "Bússola do Capitão", que rastreia o progresso
 * e a identidade ideológica do jogador através do mundo.
 */
@Getter
@Setter
@ToString(exclude = "ship")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome do capitão (jogador).
     */
    @Column(name = "captain_name")
    private String captainName;

    /**
     * A quantidade de ouro (dinheiro) que o jogador possui.
     */
    @Builder.Default
    private Integer gold = 500;

    /**
     * Reputação (0-1000): A posição do jogador dentro do sistema opressor.
     * Aumenta ao servir os interesses do Império e da Guilda.
     */
    @Builder.Default
    private Integer reputation = 0;

    /**
     * Infâmia (0-1000): A reputação do jogador como um predador egoísta.
     * Aumenta com a pirataria e a brutalidade.
     */
    @Builder.Default
    private Integer infamy = 0;

    /**
     * Aliança (0-1000): A reputação do jogador como um símbolo de esperança.
     * Aumenta ao ajudar os oprimidos.
     */
    @Builder.Default
    private Integer alliance = 0;

    /**
     * O navio atualmente comandado pelo jogador nesta sessão de jogo.
     * A relação é OneToOne, pois cada jogo tem apenas um navio principal por vez.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ship_id", referencedColumnName = "id")
    @JsonManagedReference("game-ship")
    private Ship ship;

    @ManyToOne
    @JoinColumn(name = "active_contract_id")
    private Contract activeContract;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_port_id")
    private Port currentPort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_port_id")
    private Port destinationPort;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_encounter_id")
    private SeaEncounter currentEncounter;

    // Tutorial-related fields
    @Enumerated(EnumType.STRING)
    @Column(name = "intro_choice")
    private IntroChoice introChoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "tutorial_phase")
    @Builder.Default
    private TutorialPhase tutorialPhase = TutorialPhase.PREPARATION_CREW;

    @Column(name = "tutorial_completed")
    @Builder.Default
    private boolean tutorialCompleted = false;

    // Tutorial checklist progress (persistent)
    @Column(name = "tutorial_crew_completed")
    @Builder.Default
    private boolean tutorialCrewCompleted = false;

    @Column(name = "tutorial_ship_completed")
    @Builder.Default
    private boolean tutorialShipCompleted = false;

    @Column(name = "tutorial_supplies_completed")
    @Builder.Default
    private boolean tutorialSuppliesCompleted = false;

    // Game Over fields
    @Column(name = "game_over")
    @Builder.Default
    private boolean gameOver = false;

    @Column(name = "game_over_reason")
    private String gameOverReason;
    
    // Captain Progression fields
    @Column(name = "captain_level")
    @Builder.Default
    private Integer captainLevel = 1;
    
    @Column(name = "captain_xp")
    @Builder.Default
    private Integer captainXP = 0;
    
    @Column(name = "captain_skill_points")
    @Builder.Default
    private Integer captainSkillPoints = 0;
    
    // Captain Skills - Combat Tree
    @Column(name = "skill_combat_prowess")
    @Builder.Default
    private Integer skillCombatProwess = 0; // +damage in battle
    
    @Column(name = "skill_naval_tactics")
    @Builder.Default
    private Integer skillNavalTactics = 0; // +critical chance
    
    @Column(name = "skill_crew_inspiration")
    @Builder.Default
    private Integer skillCrewInspiration = 0; // +crew XP gain
    
    @Column(name = "skill_leadership")
    @Builder.Default
    private Integer skillLeadership = 0; // +crew capacity
    
    // Captain Skills - Trade Tree
    @Column(name = "skill_merchant_eye")
    @Builder.Default
    private Integer skillMerchantEye = 0; // +trade profit
    
    @Column(name = "skill_negotiation")
    @Builder.Default
    private Integer skillNegotiation = 0; // better prices
    
    @Column(name = "skill_economic_mind")
    @Builder.Default
    private Integer skillEconomicMind = 0; // +contract rewards
    
    // Captain Skills - Exploration Tree
    @Column(name = "skill_sea_knowledge")
    @Builder.Default
    private Integer skillSeaKnowledge = 0; // better encounters
    
    @Column(name = "skill_weather_reading")
    @Builder.Default
    private Integer skillWeatherReading = 0; // storm avoidance
    
    @Column(name = "skill_navigation_master")
    @Builder.Default
    private Integer skillNavigationMaster = 0; // faster travel
}
