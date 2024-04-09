package net.slqmy.castle_siege_plugin.customItems;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.util.ItemStackBuilder;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class CustomItem implements Listener {
    protected final CastleSiegePlugin plugin;

    @Getter
    protected final ItemStack itemStack;
    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.plugin = CastleSiegePlugin.getInstance();
    }

    public abstract String getIdentifier();

    protected boolean isCustomItem(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String value = pdc.get(ItemStackBuilder.KEY, PersistentDataType.STRING);

        return Objects.equals(value, getIdentifier());
    }
}
