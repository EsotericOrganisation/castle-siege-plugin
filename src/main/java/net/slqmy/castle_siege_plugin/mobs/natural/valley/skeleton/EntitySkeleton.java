package net.slqmy.castle_siege_plugin.mobs.natural.valley.skeleton;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import org.jetbrains.annotations.NotNull;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

final class EntitySkeleton extends Skeleton {
    private final CastleSiegePlugin plugin;

    private final MapRegion homeRegion;
    private final String type;

    EntitySkeleton(Level world, MapRegion region, String identifier) {
        super(EntityType.SKELETON, world);
        this.plugin = CastleSiegePlugin.getInstance();

        this.homeRegion = region;
        this.type = identifier;

        applyAttributes();
        applyModifiers();
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);

        homeRegion.decrementTotalMobs();
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);

        homeRegion.decrementTotalMobs();
    }

    private void applyAttributes() {
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        assert speed != null;

        AttributeInstance followRange = getAttribute(Attributes.FOLLOW_RANGE);
        assert followRange != null;

        AttributeInstance health = getAttribute(Attributes.MAX_HEALTH);
        assert health != null;

        speed.setBaseValue(0.3D);
        followRange.setBaseValue(48.0D);
        health.setBaseValue(30.0F);
    }

    private void applyModifiers() {
        setHealth(30.0F);
        setPersistenceRequired(true);

        plugin.getPdcManager().setStringValue(
            getBukkitEntity(), key("mobs.type"), type);
    }
}
