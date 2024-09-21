package net.slqmy.castle_siege_plugin.items.bows;

import net.slqmy.castle_siege_plugin.items.ItemIdentifier;
import net.slqmy.castle_siege_plugin.items.base.AbstractCustomItem;
import net.slqmy.castle_siege_plugin.util.itemstack.ItemStackBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

public final class LongBow extends AbstractCustomItem {
    private static final ItemStack ITEM = new ItemStackBuilder()
        .setMaterial(Material.BOW)
        .setName("<gold>Longbow")
        .setLore(
            "<dark_gray><i>Powerful bow that can reach targets",
            "<dark_gray><i>from a longer distance.",
            "",
            "<gold>Ability: Longshot",
            "<gray>Gain <red>+1" + "◎" + " Arrow Power<gray> for every <red>10",
            "<gray>blocks that the arrow travels.",
            "☹", //test
            "<gray>Fully charged shots travel <red>+25%<gray> faster."
        )
        .setUnbreakable(true)
        .setCustomStringData(ItemIdentifier.LONGBOW.getAsString())
        .build();

    public LongBow() {
        super(ITEM);
    }

    @Override
    public ItemIdentifier getIdentifier() {
        return ItemIdentifier.LONGBOW;
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

        pdcManager.setIntListValue(
            arrow, key("items$bows.arrow_origin"), location);

        if (event.getForce() == 3.0F) {
            Vector newVelocity = arrow.getVelocity().multiply(1.25);
            arrow.setVelocity(newVelocity);
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow))
            return;

        int[] location = pdcManager.getIntListValue(
            arrow, key("items$bows.arrow_origin"));

        if (location == null) return;

        double distance = arrow.getLocation().distance(
            new Location(arrow.getWorld(), location[0], location[1], location[2]));

        double arrowPower = distance / 10;
        double extraDamage = arrowPower * 1.3;

        event.setDamage(event.getDamage() + extraDamage);
    }
}
