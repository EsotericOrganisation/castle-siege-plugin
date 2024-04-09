package net.slqmy.castle_siege_plugin.game.data.player;

import lombok.Getter;
import lombok.Setter;
import net.slqmy.castle_siege_plugin.game.classes.King;
import net.slqmy.castle_siege_plugin.game.classes.base.GameClass;
import net.slqmy.castle_siege_plugin.game.data.team.CastleSiegeTeam;
import org.bukkit.entity.Player;

@Getter
public final class TeamPlayer {
    private final Player player;
    private final CastleSiegeTeam team;

    @Setter
    private GameClass gameClass;
    public TeamPlayer(Player player, CastleSiegeTeam team) {
        this.player = player;
        this.team = team;
    }

    public void equipPlayer() {
        gameClass.equipPlayer(this.player);
    }

    public boolean isKing() {
        return gameClass instanceof King;
    }
}
