package net.slqmy.castle_siege_plugin.game.data.arena;

import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.game.data.team.TeamBaseData;
import org.bukkit.Material;

import java.util.List;

public record ArenaConfig(
    String worldName,
    String locale,
    boolean parrotMailEnabled,
    boolean artilleryEnabled,
    boolean monstersEnabled,
    boolean trapsEnabled,
    boolean wildTrapsEnabled,
    List<TeamBaseData> bases,
    GlobalRegions globalRegions,
    Material windowMaterial,
    int goldDropOnKill,
    double developmentPeriodM,
    double attackingPeriodM,
    double timeBeforeAttackEndWhenWarningS,
    double timeAfterAttackEndUntilInstantKillS,
    double restPeriodM,
    DeathMatch deathMatch
    ) {
    public record DeathMatch(
        int attackingSessionsPerTeamUntilDeathMatch,
        double durationM,
        double borderShrinkRatePerTick,
        int totalBorderShrinkTicks
    ) {}

    public record GlobalRegions(
        List<MapRegion> mountains,
        List<MapRegion> valleys
    ) {}
}