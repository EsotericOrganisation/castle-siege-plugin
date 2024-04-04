package net.slqmy.castle_siege_plugin.customItems.bows;

import net.kyori.adventure.text.Component;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.customItems.CustomItem;
import net.slqmy.castle_siege_plugin.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public final class LongBow extends CustomItem implements Listener {
    private static final NamespacedKey CUSTOM_ARROW_KEY = new NamespacedKey(CastleSiegePlugin.getInstance(), "custom_arrow");
    private static final NamespacedKey ARROW_SHOT_LOCATION_KEY = new NamespacedKey(CastleSiegePlugin.getInstance(), "arrow_shot_location");
    private static final String IDENTIFIER = "longbow";
    private static final String SHOOT_BOW_EVENT = EntityShootBowEvent.class.getSimpleName();
    private static final ItemStack ITEM = new ItemStackBuilder()
        .setMaterial(Material.BOW)
        .setName("<gold>Longbow")
        .setLore(
            "<dark_gray><i>Powerful bow that can reach targets",
            "<dark_gray><i>from a longer distance.",
            "",
            "<yellow>Ability: Longshot",
            "<gray>Gain <red>+1◎ Arrow Power<gray> for every <red>10",
            "<gray>blocks that the arrow travels.",
            "",
            "<gray>Fully charged shots travel <red>+30%<gray> faster."
        )
        .setUnbreakable(true)
        .setCustomData(IDENTIFIER)
        .build();
    public LongBow(CastleSiegePlugin plugin) {
        super(ITEM, plugin);

        registerIntents();
        registerEventHandlers();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public void registerIntents() {
        registerIntent(SHOOT_BOW_EVENT);
    }

    @Override
    public void registerEventHandlers() {
        registerEventHandler(SHOOT_BOW_EVENT, event -> {
            EntityShootBowEvent bowEvent = (EntityShootBowEvent) event;
            Entity arrow = bowEvent.getProjectile();

            int[] location = new int[] {
                arrow.getLocation().getBlockX(),
                arrow.getLocation().getBlockY(),
                arrow.getLocation().getBlockZ()
            };

            PersistentDataContainer pdc = arrow.getPersistentDataContainer();
            pdc.set(CUSTOM_ARROW_KEY, PersistentDataType.BOOLEAN, true);
            pdc.set(ARROW_SHOT_LOCATION_KEY, PersistentDataType.INTEGER_ARRAY, location);

            if (bowEvent.getForce() == 1.0F) {
                Vector newVelocity = arrow.getVelocity().multiply(1.3);
                arrow.setVelocity(newVelocity);
            }
        });
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }

        if (arrow.getPersistentDataContainer().has(CUSTOM_ARROW_KEY, PersistentDataType.BOOLEAN)) {
            int[] location = arrow.getPersistentDataContainer().get(ARROW_SHOT_LOCATION_KEY, PersistentDataType.INTEGER_ARRAY);
            assert location != null;

            double distance = arrow.getLocation().distance(
                new Location(arrow.getWorld(), location[0], location[1], location[2])
            );
            double arrowPower = distance / 10;
            double extraDamage = arrowPower * 2;

            Bukkit.broadcast(Component.text(extraDamage));
            event.setDamage(event.getDamage() + extraDamage);
        }
    }
}
