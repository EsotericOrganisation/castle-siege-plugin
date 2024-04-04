package net.slqmy.castle_siege_plugin.customItems;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public abstract class CustomItem {
    protected final CastleSiegePlugin plugin;
    @Getter
    protected final ItemStack itemStack;
    @Getter
    protected final List<String> intents;
    @Getter
    protected final HashMap<String, Consumer<Event>> eventHandlers;
    public CustomItem(ItemStack itemStack, CastleSiegePlugin plugin) {
        this.itemStack = itemStack;
        this.plugin = plugin;
        this.intents = new ArrayList<>();
        this.eventHandlers = new HashMap<>();
    }

    public abstract String getIdentifier();
    public abstract void registerIntents();
    public abstract void registerEventHandlers();
    protected void registerIntent(String eventName) {
        intents.add(eventName);
    }
    protected void registerEventHandler(String eventName, Consumer<Event> eventHandler) {
        eventHandlers.put(eventName, eventHandler);
    }
}
