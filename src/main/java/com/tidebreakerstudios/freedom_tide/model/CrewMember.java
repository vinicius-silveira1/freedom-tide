package com.tidebreakerstudios.freedom_tide.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewProfession;
import com.tidebreakerstudios.freedom_tide.model.enums.CrewRank;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa um membro individual da tripulação do jogador.
 * Cada membro tem seus próprios atributos, personalidade e estado moral,
 * refletindo a face humana da desigualdade no mundo do jogo.
 * 
 * Sistema de Progressão:
 * - Cada tripulante pode evoluir em sua profissão através de experiência
 * - Profissões são determinadas pelos atributos dominantes
 * - Ranks são alcançados através de acúmulo de XP em ações específicas
 */
@Getter
@Setter
@ToString(exclude = "ship")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crew_members")
public class CrewMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O nome do membro da tripulação.
     */
    private String name;

    /**
     * Background/história pessoal do tripulante.
     * Usado para criar conexão emocional e narrativa.
     */
    @Column(length = 500)
    private String background;

    /**
     * Frase característica do personagem.
     */
    @Column(length = 200)
    private String catchphrase;

    // --- Atributos (1-10) ---
    private Integer navigation;
    private Integer artillery;
    private Integer combat;
    private Integer medicine;
    private Integer carpentry;
    private Integer intelligence;

    /**
     * Nível de Desespero no momento do recrutamento.
     * Um valor alto significa salário mais baixo, mas maior risco de problemas.
     */
    private Integer despairLevel;

    /**
     * A personalidade ideológica do tripulante, que afeta seu moral
     * com base nas ações do capitão.
     */
    @Enumerated(EnumType.STRING)
    private CrewPersonality personality;

    // === SISTEMA DE PROGRESSÃO ===

    /**
     * A profissão/especialização do tripulante.
     * Determinada automaticamente pelos atributos dominantes.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CrewProfession profession = CrewProfession.SAILOR;

    /**
     * O rank/nível atual do tripulante em sua profissão.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CrewRank rank = CrewRank.RECRUIT;

    /**
     * Pontos de experiência acumulados.
     * Usados para evoluir de rank na profissão.
     */
    @Builder.Default
    private Integer experiencePoints = 0;

    /**
     * Número total de combates navais participados.
     */
    @Builder.Default
    private Integer combatsParticipated = 0;

    /**
     * Número total de viagens completadas.
     */
    @Builder.Default
    private Integer journeysCompleted = 0;

    /**
     * Número total de reparos realizados no navio.
     */
    @Builder.Default
    private Integer repairsPerformed = 0;

    /**
     * Número total de tratamentos médicos realizados.
     */
    @Builder.Default
    private Integer healingsPerformed = 0;

    /**
     * Habilidades especiais desbloqueadas por este tripulante.
     * Armazenadas como string JSON para flexibilidade.
     */
    @Column(length = 1000)
    private String unlockedAbilities;

    /**
     * O moral individual do tripulante (0-100).
     * Contribui para o moral geral da tripulação.
     */
    private Integer moral;

    /**
    * A lealdade do tripulante para com o capitão (0-100).
    * Pondera a influência do seu moral no cálculo do moral geral.
    */
    @Builder.Default
    private Integer loyalty = 50;

    /**
     * O salário em ouro que o tripulante recebe a cada ciclo de pagamento.
     */
    private Integer salary;

    /**
     * O navio ao qual este membro da tripulação pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id")
    @JsonBackReference("ship-crew")
    private Ship ship;

    // === MÉTODOS DE PROGRESSÃO ===

    /**
     * Atualiza a profissão baseada nos atributos atuais.
     * Deve ser chamado sempre que os atributos mudarem.
     */
    public void updateProfession() {
        this.profession = CrewProfession.determineProfession(
            navigation, artillery, combat, medicine, carpentry, intelligence
        );
    }

    /**
     * Adiciona pontos de experiência e verifica se pode evoluir de rank.
     */
    public boolean gainExperience(int xpGained) {
        this.experiencePoints += xpGained;
        return checkRankUp();
    }

    /**
     * Verifica se o tripulante pode evoluir de rank e aplica a evolução.
     * @return true se evoluiu, false caso contrário
     */
    public boolean checkRankUp() {
        CrewRank nextRank = this.rank.getNextRank(this.profession);
        
        if (nextRank != this.rank && this.experiencePoints >= nextRank.getRequiredXP()) {
            this.rank = nextRank;
            return true;
        }
        return false;
    }

    /**
     * Calcula a soma total dos atributos.
     */
    public int getTotalAttributes() {
        return navigation + artillery + combat + medicine + carpentry + intelligence;
    }

    /**
     * Retorna o atributo principal baseado na profissão.
     */
    public int getPrimaryAttribute() {
        return switch (this.profession) {
            case NAVIGATOR -> navigation;
            case GUNNER -> artillery;
            case FIGHTER -> combat;
            case MEDIC -> medicine;
            case CARPENTER -> carpentry;
            case STRATEGIST -> intelligence;
            case CORSAIR -> Math.max(combat, artillery);
            case EXPLORER -> Math.max(navigation, intelligence);
            case BATTLE_MEDIC -> Math.max(medicine, combat);
            case SAILOR -> getTotalAttributes() / 6; // Média
        };
    }

    /**
     * Verifica se o tripulante tem uma habilidade específica desbloqueada.
     */
    public boolean hasAbility(String abilityName) {
        return unlockedAbilities != null && unlockedAbilities.contains(abilityName);
    }

    /**
     * Adiciona uma nova habilidade às habilidades desbloqueadas.
     */
    public void unlockAbility(String abilityName) {
        if (unlockedAbilities == null) {
            unlockedAbilities = abilityName;
        } else if (!hasAbility(abilityName)) {
            unlockedAbilities += "," + abilityName;
        }
    }

    /**
     * Retorna uma descrição completa do progresso do tripulante.
     */
    public String getProgressSummary() {
        return String.format("%s - %s %s (XP: %d/%d)", 
            name, 
            profession.getDisplayName(),
            rank.getDisplayName(),
            experiencePoints,
            rank.getNextRank(profession).getRequiredXP()
        );
    }
}
