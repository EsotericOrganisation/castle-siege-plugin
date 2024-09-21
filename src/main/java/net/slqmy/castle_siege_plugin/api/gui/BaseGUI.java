package net.slqmy.castle_siege_plugin.api.gui;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.managers.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class BaseGUI implements Listener {
    protected final CastleSiegePlugin plugin;
    protected final PersistentDataManager pdcManager;

    protected final Inventory gui;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickListeners;

    protected Consumer<InventoryOpenEvent> onOpen;
    protected Consumer<InventoryCloseEvent> onClose;

    protected boolean frozen;

    protected Player player;

    public BaseGUI(Component title, int rows) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.pdcManager = plugin.getPdcManager();

        this.gui = Bukkit.createInventory(null, rows * 9, title);
        this.clickListeners = new HashMap<>();
    }

    public void show(Player player) {
        player.openInventory(gui);

        TeamPlayer teamPlayer = TeamPlayer.getFrom(player);
        if (teamPlayer != null)
            teamPlayer.setOpenGui(this);
    }

    public void setItem(int slot, ItemStack item) {
        gui.setItem(slot, item);
    }

    public void setItems(ItemStack item, int... slots) {
        for (int i : slots) {
            gui.setItem(i, item);
        }
    }

    public void setOnClick(int slot, Consumer<InventoryClickEvent> onClick) {
        clickListeners.put(slot, onClick);
    }

    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (isValidGUI(event) && onOpen != null) {
            onOpen.accept(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (isValidGUI(event)) {
            HandlerList.unregisterAll(this);

            if (onClose != null) {
                onClose.accept(event);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isValidGUI(event) && gui.equals(event.getClickedInventory())) {
            int slot = event.getSlot();

            if (clickListeners.containsKey(slot)) {
                clickListeners.get(slot).accept(event);
            }
        }

        if (frozen) event.setCancelled(true);
    }

    protected boolean isValidGUI(InventoryEvent event) {
        return event.getInventory().equals(gui);
    }
}
