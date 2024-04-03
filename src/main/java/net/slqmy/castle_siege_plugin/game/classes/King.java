package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.entity.Player;

public class King extends GameClass {

    public King(CastleSiegePlugin plugin) {
        super(plugin);
    }

    @Override
    public int getPlayerCount(int totalPlayerCount) {
        return 1;
    }

    @Override
    public void equipPlayer(Player player) {

    }
}
