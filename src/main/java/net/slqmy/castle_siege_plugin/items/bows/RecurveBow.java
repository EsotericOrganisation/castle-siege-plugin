package net.slqmy.castle_siege_plugin.items.bows;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.level.Level;
import net.slqmy.castle_siege_plugin.items.ItemIdentifier;
import net.slqmy.castle_siege_plugin.items.base.AbstractCustomItem;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import net.slqmy.castle_siege_plugin.util.itemstack.ItemStackBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class RecurveBow extends AbstractCustomItem {
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
            "<gray>Arrows travel <red>+12.5%<gray> faster."
        )
        .setUnbreakable(true)
        .setCustomStringData(ItemIdentifier.RECURVE_BOW.getAsString())
        .build();

    public RecurveBow() {
        super(ITEM);
    }

    @Override
    public ItemIdentifier getIdentifier() {
        return ItemIdentifier.RECURVE_BOW;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (!isCustomItem(bow)) return;

        ItemStack consumable = event.getConsumable();
        assert consumable != null;

        float force = event.getForce();
        Entity bukkitEntity = event.getEntity();

        LivingEntity shooter = NMSUtil.toNMSLivingEntity(event.getEntity());
        AbstractArrow arrow = (AbstractArrow) NMSUtil.toNMSEntity(event.getProjectile());

        ArrowItem arrowItem = (ArrowItem) NMSUtil.toNMSItemStack(consumable).getItem();
        Level level = shooter.level();

        AbstractArrow newArrow = arrowItem.createArrow(
            level, NMSUtil.toNMSItemStack(consumable), shooter);

        level.addFreshEntity(newArrow);

        if (bukkitEntity instanceof Player player) {
            newArrow.shootFromRotation(
                shooter, shooter.getXRot(), shooter.getYRot(),
                0.0F, force == 3.0F ? force * 1.125F : force, 0.0F);

            newArrow.setCritArrow(arrow.isCritArrow());
            newArrow.setKnockback(arrow.getKnockback());
            newArrow.setBaseDamage(arrow.getBaseDamage());
            newArrow.setRemainingFireTicks(arrow.getRemainingFireTicks());

            level.playSound(
                null,
                shooter.getX(), shooter.getY(), shooter.getZ(),
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + force / 6.0F
            );

            if (event.shouldConsumeItem() && player.getGameMode() != GameMode.CREATIVE) {
                consumable.add(-1);
            }

            ((net.minecraft.world.entity.player.Player) shooter).awardStat(
                Stats.ITEM_USED.get(NMSUtil.toNMSItemStack(bow).getItem()));
        } else if (shooter instanceof AbstractSkeleton skeleton) {
            LivingEntity target = skeleton.getTarget();
            if (target == null) return;

            double x = target.getX() - skeleton.getX();
            double y = target.getY(0.33D) - newArrow.getY();
            double z = target.getZ() - skeleton.getZ();
            double horizontalDistance = Math.sqrt(x * x + z * z);

            newArrow.shoot(x, y + horizontalDistance * 0.2D, z, 1.9F * 1.125F, 0.0F);

            newArrow.setKnockback(arrow.getKnockback());
            newArrow.setBaseDamage(arrow.getBaseDamage());
            newArrow.setRemainingFireTicks(arrow.getRemainingFireTicks());
        }

        event.setCancelled(true);
    }
}
