package net.slqmy.castle_siege_plugin.managers;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.customItems.CustomItem;
import org.bukkit.event.Listener;

import java.util.HashMap;

public final class CustomItemManager implements Listener {
    private final CastleSiegePlugin plugin;
    @Getter
    private final HashMap<String, CustomItem> identifierToItemMap;
    public CustomItemManager() {
        this.plugin = CastleSiegePlugin.getInstance();
        this.identifierToItemMap = new HashMap<>();
    }

    public void registerCustomItem(CustomItem item) {
        identifierToItemMap.put(item.getIdentifier(), item);
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
    }
}
