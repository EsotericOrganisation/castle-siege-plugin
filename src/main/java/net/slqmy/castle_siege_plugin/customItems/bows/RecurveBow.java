package net.slqmy.castle_siege_plugin.customItems.bows;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.level.Level;
import net.slqmy.castle_siege_plugin.customItems.CustomItem;
import net.slqmy.castle_siege_plugin.util.ItemStackBuilder;
import net.slqmy.castle_siege_plugin.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class RecurveBow extends CustomItem {
    private static final String IDENTIFIER = "recurve_bow";
    private static final ItemStack ITEM = new ItemStackBuilder()
        .setMaterial(Material.BOW)
        .setName("<dark_purple>Recurve Bow")
        .setLore(
            "<dark_gray><i>Prestigous bow that shoots arrows",
            "<dark_gray><i>at pinpoint accuracy.",
            "",
            "<gold>Ability: Absolute Precision",
            "<gray>Arrows fired from this bow have",
            "<gold>absolute<gray> precision.",
            "",
            "<gray>Arrows travel <red>+10%<gray> faster."
        )
        .setUnbreakable(true)
        .setCustomData(IDENTIFIER)
        .build();
    public RecurveBow() {
        super(ITEM);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (!isCustomItem(bow)) return;

        event.setCancelled(true);

        ItemStack consumable = event.getConsumable();
        assert consumable != null;

        float force = event.getForce();

        net.minecraft.world.entity.LivingEntity shooter = Util.toNMSLivingEntity(event.getEntity());
        ArrowItem arrowItem = (ArrowItem) Util.toNMSItemStack(consumable).getItem();
        Level level = shooter.level();

        AbstractArrow newArrow = arrowItem.createArrow(
            level,
            Util.toNMSItemStack(consumable),
            shooter
        );

        newArrow.shootFromRotation(
            shooter,
            shooter.getXRot(),
            shooter.getYRot(),
            0.0F,
            force * 3.0F,
            0.0F
        );

        level.addFreshEntity(newArrow);

        if (force == 1.0F) {
            newArrow.setCritArrow(true);
            newArrow.getBukkitEntity().setVelocity(newArrow.getBukkitEntity().getVelocity().multiply(1.1));
        }

        level.playSound(
            null,
            shooter.getX(), shooter.getY(), shooter.getZ(),
            SoundEvents.ARROW_SHOOT,
            SoundSource.PLAYERS,
            1.0F,
            1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + force * 0.5F
        );

        if (event.shouldConsumeItem() && (event.getEntity() instanceof Player player) && !player.getGameMode().equals(GameMode.CREATIVE)) {
            consumable.add(-1);
        }

        assert bow != null;

        if (shooter instanceof net.minecraft.world.entity.player.Player player) {
            player.awardStat(Stats.ITEM_USED.get(Util.toNMSItemStack(bow).getItem()));
        }
    }
}
