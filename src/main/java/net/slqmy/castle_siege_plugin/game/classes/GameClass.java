package net.slqmy.castle_siege_plugin.game.classes;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;

public abstract class GameClass {

    protected final CastleSiegePlugin plugin;

    public GameClass(CastleSiegePlugin plugin) {
        this.plugin = plugin;
    }

    public abstract int getPlayerCount(int totalPlayerCount);
}
