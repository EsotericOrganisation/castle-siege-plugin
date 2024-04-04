package net.slqmy.castle_siege_plugin;

import net.slqmy.castle_siege_plugin.managers.ArenaManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CastleSiegePlugin extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onLoad() {
        arenaManager = new ArenaManager(this);
    }
}
