package net.slqmy.castle_siege_plugin.events.bukkit;

import net.slqmy.castle_siege_plugin.events.base.AbstractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener extends AbstractEvent {
    public BlockBreakListener() {
        super();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getArenaManager().getArenas().containsKey(event.getBlock().getWorld().getUID())) {
            event.setCancelled(true);
        }
    }
}
