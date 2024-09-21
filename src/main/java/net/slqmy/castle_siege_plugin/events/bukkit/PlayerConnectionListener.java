package net.slqmy.castle_siege_plugin.events.bukkit;

import net.slqmy.castle_siege_plugin.events.base.AbstractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerConnectionListener extends AbstractEvent {
    public PlayerConnectionListener() {
        super();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPacketInterceptor().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPacketInterceptor().removePlayer(event.getPlayer());
    }
}
