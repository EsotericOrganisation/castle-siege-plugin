package net.slqmy.castle_siege_plugin.game.data.arena;

import lombok.Getter;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public final class CastleSiegeArenaConfig {
    private String worldName;
    private String locale;
    private boolean parrotMailEnabled;
    private boolean artilleryEnabled;
    private boolean monstersEnabled;
    private boolean trapsEnabled;
    private boolean wildTrapsEnabled;
    private List<CastleSiegeTeamBaseData> bases;
    private String windowMaterial;
    private int goldDropOnKill;
    private double developmentPeriodM;
    private double attackingPeriodM;
    private double timeAfterAttackEndUntilInstantKillS;
    private double restPeriodM;
    private DeathMatch deathMatch;

    @Getter
    public static class DeathMatch {
        private int attackingSessionsPerTeamUntilDeathMatch;
        private double durationM;
        private double borderShrinkRatePerTick;
        private int totalBorderShrinkTicks;
    }
}
