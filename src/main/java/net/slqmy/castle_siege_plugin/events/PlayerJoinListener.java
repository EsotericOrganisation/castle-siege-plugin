package net.slqmy.castle_siege_plugin.events;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {
    private final CastleSiegePlugin plugin;
    public PlayerJoinListener() {
        this.plugin = CastleSiegePlugin.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().addItem(plugin.getCustomItemManager().getIdentifierToItemMap().get("long_bow").getItemStack());
        player.getInventory().addItem(plugin.getCustomItemManager().getIdentifierToItemMap().get("recurve_bow").getItemStack());
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
