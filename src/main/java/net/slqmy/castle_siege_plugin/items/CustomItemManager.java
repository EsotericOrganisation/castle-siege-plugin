package net.slqmy.castle_siege_plugin.items;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.items.base.AbstractCustomItem;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class CustomItemManager implements Listener {
    private final Map<ItemIdentifier, AbstractCustomItem> customItems;

    public CustomItemManager() {
        this.customItems = new HashMap<>();
    }

    public void registerCustomItem(AbstractCustomItem item) {
        customItems.put(item.getIdentifier(), item);
        item.registerEvents();
    }

    public AbstractCustomItem getCustomItem(ItemIdentifier identifier) {
        return customItems.get(identifier);
    }

    public ItemStack getCustomItemStack(ItemIdentifier identifier) {
        return getCustomItem(identifier).getItemStack();
    }

    public void unregisterAll() {
        customItems.forEach((identifier, item) -> item.unregisterEvents());
        customItems.clear();
    }
}
