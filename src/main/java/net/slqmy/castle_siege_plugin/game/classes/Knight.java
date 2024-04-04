package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.entity.Player;

public class Knight extends CustomisableGameClass {
    private static final String NAME = "Knight";
    public Knight(CastleSiegePlugin plugin) {
        super(plugin, NAME);
    }

    @Override
    public void equipPlayer(Player player) {

    }
}
