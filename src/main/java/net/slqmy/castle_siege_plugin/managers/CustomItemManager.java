package net.slqmy.castle_siege_plugin.managers;

import lombok.Getter;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.customItems.CustomItem;
import net.slqmy.castle_siege_plugin.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CustomItemManager implements Listener {
    private final CastleSiegePlugin plugin;
    private final HashMap<String, List<CustomItem>> eventToItemMap;
    @Getter
    private final HashMap<String, CustomItem> identifierToItemMap;
    public CustomItemManager(CastleSiegePlugin plugin) {
        this.plugin = plugin;
        this.eventToItemMap = new HashMap<>();
        this.identifierToItemMap = new HashMap<>();
    }

    public void init() {
        eventToItemMap.put(EntityDamageEvent.class.getSimpleName(), new ArrayList<>()); // Mainly Armours and defense items
        eventToItemMap.put(EntityDamageByEntityEvent.class.getSimpleName(), new ArrayList<>()); // Mainly Weapons
        eventToItemMap.put(EntityShootBowEvent.class.getSimpleName(), new ArrayList<>()); // Bows
        eventToItemMap.put(PlayerItemConsumeEvent.class.getSimpleName(), new ArrayList<>()); // Food and Potions

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerCustomItem(CustomItem item) {
        List<String> intents = item.getIntents();

        for (String eventName : intents) {
            if (!eventToItemMap.containsKey(eventName)) {
                plugin.getLogger().warning("Unsupported intent for event: " + eventName);
                continue;
            }

            List<CustomItem> items = eventToItemMap.get(eventName);
            items.add(item);
        }

        identifierToItemMap.put(item.getIdentifier(), item);

        //Allow handling of extra events managed by the custom item.
        if (item instanceof Listener listener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @EventHandler // Calls event handlers for all armour pieces of the damaged entity, if any
    public void onEntityDamage(EntityDamageEvent event) {
        String eventName = event.getClass().getSimpleName();

        if (!eventToItemMap.containsKey(eventName)) return;
        List<CustomItem> items = eventToItemMap.get(eventName);

        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        ItemStack[] armourContents = equipment.getArmorContents();
        for (ItemStack armourPiece : armourContents) {
            for (CustomItem item : items) {
                if (isMatchingCustomItem(armourPiece, item)) {
                    item.getEventHandlers().get(eventName).accept(event);
                }
            }
        }
    }

    @EventHandler //Calls the event handler for the weapon used, if any
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        String eventName = event.getClass().getSimpleName();

        if (!eventToItemMap.containsKey(eventName)) return;
        List<CustomItem> items = eventToItemMap.get(eventName);

        if (!(event.getDamager() instanceof LivingEntity damager)) {
            return;
        }

        EntityEquipment equipment = damager.getEquipment();
        if (equipment == null) return;

        ItemStack usedItem = equipment.getItemInMainHand();

        for (CustomItem item : items) {
            if (isMatchingCustomItem(usedItem, item)) {
                item.getEventHandlers().get(eventName).accept(event);
            }
        }
    }

    @EventHandler // Calls the event handler for the bow used, unless it's a crossbow
    public void onEntityShootBow(EntityShootBowEvent event) {
        String eventName = event.getClass().getSimpleName();
        if (!eventToItemMap.containsKey(eventName)) return;

        List<CustomItem> items = eventToItemMap.get(eventName);

        ItemStack usedItem = event.getBow();
        if (usedItem == null || !usedItem.getType().equals(Material.BOW)) return;

        for (CustomItem item : items) {
            if (isMatchingCustomItem(usedItem, item)) {
                item.getEventHandlers().get(eventName).accept(event);
            }
        }
    }

    @EventHandler // Calls the event handler for the item consumed
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        String eventName = event.getClass().getSimpleName();
        if (!eventToItemMap.containsKey(eventName)) return;

        List<CustomItem> items = eventToItemMap.get(eventName);
        ItemStack usedItem = event.getItem();

        for (CustomItem item : items) {
            if (isMatchingCustomItem(usedItem, item)) {
                item.getEventHandlers().get(eventName).accept(event);
            }
        }
    }

    private boolean isMatchingCustomItem(ItemStack item, CustomItem customItem) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String value = pdc.get(ItemStackBuilder.KEY, PersistentDataType.STRING);

        return value != null && value.equals(customItem.getIdentifier());
    }
}
