package net.slqmy.castle_siege_plugin.game.data;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.game.teams.CastleSiegeTeam;
import org.bukkit.entity.Player;

@Getter
public final class TeamPlayer {
    private final Player player;
    private final CastleSiegeTeam team;
    public TeamPlayer(Player player, CastleSiegeTeam team) {
        this.player = player;
        this.team = team;
    }
}
