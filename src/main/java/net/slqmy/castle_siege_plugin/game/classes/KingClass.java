package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;

public class KingClass extends GameClass {

    public KingClass(CastleSiegePlugin plugin) {
        super(plugin);
    }

    @Override
    public int getPlayerCount(int totalPlayerCount) {
        return 1;
    }
}
