package net.slqmy.castle_siege_plugin.game.data;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

@Getter
public class CastleSiegeTeam {
    private final static Random random = new Random();

    private List<Player> players;

    private CastleSiegeTeamBase teamBase;

    public CastleSiegeTeam(CastleSiegeTeamBase teamBase) {
        this.teamBase = teamBase;
    }

    public void spawnPlayers() {
        List<Location> spawnPoints = teamBase.getSpawnPoints();

        for (Player player : players) {
            player.teleport(spawnPoints.get(random.nextInt(spawnPoints.size())));
        }
    }
}
