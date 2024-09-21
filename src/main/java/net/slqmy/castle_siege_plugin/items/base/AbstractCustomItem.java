package net.slqmy.castle_siege_plugin.items.base;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.items.ItemIdentifier;
import net.slqmy.castle_siege_plugin.managers.PersistentDataManager;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

public abstract class AbstractCustomItem implements Listener {
    protected final CastleSiegePlugin plugin;
    protected final PersistentDataManager pdcManager;

    @Getter
    protected final ItemStack itemStack;

    public AbstractCustomItem(ItemStack itemStack) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.pdcManager = plugin.getPdcManager();

        this.itemStack = itemStack;
    }

    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public net.minecraft.world.item.ItemStack toNMS() {
        return NMSUtil.toNMSItemStack(itemStack);
    }

    protected boolean isCustomItem(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        String value = plugin.getPdcManager().getStringValue(meta, key("items.data"));
        return Objects.equals(value, getIdentifier().getAsString());
    }

    public abstract ItemIdentifier getIdentifier();
}
