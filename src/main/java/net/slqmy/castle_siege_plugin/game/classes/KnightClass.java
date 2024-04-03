package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.entity.Player;

public class KnightClass extends GameClass {
    public KnightClass(CastleSiegePlugin plugin) {
        super(plugin);
    }

    @Override
    public int getPlayerCount(int totalPlayerCount) {
        int percentage = 10; //get from config;
        return fromPercentage(percentage, totalPlayerCount);
    }

    @Override
    public void equipPlayer(Player player) {

    }
}
