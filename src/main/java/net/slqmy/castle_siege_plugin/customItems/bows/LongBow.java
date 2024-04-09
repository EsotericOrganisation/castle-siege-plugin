package net.slqmy.castle_siege_plugin.customItems.bows;

import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.customItems.CustomItem;
import net.slqmy.castle_siege_plugin.util.ItemStackBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public final class LongBow extends CustomItem {
    private static final NamespacedKey CUSTOM_ARROW_KEY = new NamespacedKey(CastleSiegePlugin.getInstance(), "custom_arrow");
    private static final NamespacedKey ARROW_SHOT_LOCATION_KEY = new NamespacedKey(CastleSiegePlugin.getInstance(), "arrow_shot_location");
    private static final String IDENTIFIER = "long_bow";
    private static final ItemStack ITEM = new ItemStackBuilder()
        .setMaterial(Material.BOW)
        .setName("<gold>Longbow")
        .setLore(
            "<dark_gray><i>Powerful bow that can reach targets",
            "<dark_gray><i>from a longer distance.",
            "",
            "<gold>Ability: Longshot",
            "<gray>Gain <red>+1◎ Arrow Power<gray> for every <red>10",
            "<gray>blocks that the arrow travels.",
            "",
            "<gray>Fully charged shots travel <red>+25%<gray> faster."
        )
        .setUnbreakable(true)
        .setCustomData(IDENTIFIER)
        .build();
    public LongBow() {
        super(ITEM);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!isCustomItem(event.getBow())) return;

        Entity arrow = event.getProjectile();

        int[] location = new int[] {
            arrow.getLocation().getBlockX(),
            arrow.getLocation().getBlockY(),
            arrow.getLocation().getBlockZ()
        };

        PersistentDataContainer pdc = arrow.getPersistentDataContainer();
        pdc.set(CUSTOM_ARROW_KEY, PersistentDataType.BOOLEAN, true);
        pdc.set(ARROW_SHOT_LOCATION_KEY, PersistentDataType.INTEGER_ARRAY, location);

        if (event.getForce() == 1.0F) {
            Vector newVelocity = arrow.getVelocity().multiply(1.25);
            arrow.setVelocity(newVelocity);
        }
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

            event.setDamage(event.getDamage() + extraDamage);
        }
    }
}
