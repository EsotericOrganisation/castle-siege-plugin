package net.slqmy.castle_siege_plugin.events;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final CastleSiegePlugin plugin;
    public BlockBreakListener() {
        this.plugin = CastleSiegePlugin.getInstance();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equals("minigame")) {
            event.setCancelled(true);
        }
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
