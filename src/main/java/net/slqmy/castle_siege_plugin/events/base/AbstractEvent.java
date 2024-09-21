package net.slqmy.castle_siege_plugin.events.base;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.event.Listener;

public class AbstractEvent implements Listener {
    protected final CastleSiegePlugin plugin;

    public AbstractEvent() {
        this.plugin = CastleSiegePlugin.getInstance();
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
