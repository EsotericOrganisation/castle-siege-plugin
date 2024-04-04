package net.slqmy.castle_siege_plugin.game.teams;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.game.data.CastleSiegeTeamBase;
import net.slqmy.castle_siege_plugin.game.data.TeamPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class CastleSiegeTeam {
    private final static Random random = new Random();
    private final CastleSiegeTeamBase teamBase;
    private final List<TeamPlayer> teamPlayers;

    public CastleSiegeTeam(CastleSiegeTeamBase teamBase) {
        this.teamBase = teamBase;
        this.teamPlayers = new ArrayList<>();
    }

    public void spawnPlayers() {
        List<Location> spawnPoints = teamBase.getSpawnPoints();

        for (TeamPlayer teamPlayer : teamPlayers) {
            teamPlayer.getPlayer().teleport(spawnPoints.get(random.nextInt(spawnPoints.size())));
        }
    }
}
