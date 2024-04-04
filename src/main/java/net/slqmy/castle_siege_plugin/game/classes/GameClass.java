package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.entity.Player;

public abstract class GameClass {
    protected final CastleSiegePlugin plugin;
    public GameClass(CastleSiegePlugin plugin) {
        this.plugin = plugin;
    }

    public int getPlayerCount(int totalPlayerCount) {
        return 0;
    }

    public abstract void equipPlayer(Player player);

}
