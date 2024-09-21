package net.slqmy.castle_siege_plugin.mobs.natural.valley.skeleton;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.world.level.Level;
import net.slqmy.castle_siege_plugin.game.Game;
import net.slqmy.castle_siege_plugin.game.data.region.MapRegion;
import net.slqmy.castle_siege_plugin.items.CustomItemManager;
import net.slqmy.castle_siege_plugin.items.ItemIdentifier;
import net.slqmy.castle_siege_plugin.mobs.MobType;
import net.slqmy.castle_siege_plugin.mobs.base.AbstractCustomMob;
import net.slqmy.castle_siege_plugin.mobs.base.INMSMob;
import net.slqmy.castle_siege_plugin.mobs.data.MobEnvironment;
import net.slqmy.castle_siege_plugin.mobs.data.MobEquipment;
import net.slqmy.castle_siege_plugin.util.ListUtil;
import net.slqmy.castle_siege_plugin.util.region.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

import static net.slqmy.castle_siege_plugin.managers.PersistentDataManager.key;

@Getter
public final class ValleySkeleton extends AbstractCustomMob {
    private static final Random random = new Random();
    private static final MobType MOB_TYPE = MobType.VALLEY_SKELETON;

    private final CustomItemManager itemManager;
    private final MobEquipment equipment;
    private final Cuboid regionArea;

    @Getter(value = AccessLevel.NONE)
    private final EntitySkeleton handle;

    public ValleySkeleton(Level world, MapRegion region, Game game) {
        super(region, game);
        this.itemManager = game.getItemManager();
        this.handle = new EntitySkeleton(world, homeRegion, MOB_TYPE.getName());
        this.equipment = new MobEquipment();
        this.regionArea = homeRegion.asCuboid(handle.level().getWorld());

        createMobData(MOB_TYPE, 5);
        loadEquipment();
    }

    public ValleySkeleton(Game game) {
        super(new MapRegion(MobEnvironment.VALLEY), game);
        this.itemManager = game.getItemManager();
        this.handle = null;
        this.equipment = null;
        this.regionArea = null;

        createMobData(MOB_TYPE, 5);
    }

    @Override
    public INMSMob newInstance(Level world, MapRegion region, Game game) {
        return new ValleySkeleton(world, region, game);
    }

    @Override
    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void prepare() {
        findLocation();
        equip();
    }

    @Override
    public void spawn() {
        handle.level().addFreshEntity(handle);
    }

    @Override
    public Entity getHandle() {
        return handle.getBukkitEntity();
    }

    private void loadEquipment() {
        equipment.setMainHand(itemManager.getCustomItem(ItemIdentifier.RECURVE_BOW).toNMS());
    }

    private void equip() {
        equipment.equipMob(handle);
    }

    private void findLocation() {
        Location location = regionArea.findRandomSurface(100, this::isValidSpawn);

        if (location != null) {
            this.readyForSpawn = true;

            handle.setPos(location.getX(), location.getY(), location.getZ());
        } else {
            this.readyForSpawn = false;
        }
    }

    private boolean isValidSpawn(Block block) {
        boolean isPassable =
            block.getRelative(0, 1, 0).isPassable() &&
                block.getRelative(0, 2, 0).getType().isAir();

        boolean isValidMaterial =
            block.getType() == Material.STONE ||
            block.getType() == Material.GRASS_BLOCK;

        boolean hasNoNearbyPlayers = block.getLocation().getNearbyPlayers(8).isEmpty();

        return isPassable && isValidMaterial && hasNoNearbyPlayers;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        String type = pdcManager.getStringValue(entity, key("mobs.type"));

        if (MOB_TYPE.getName().equals(type)) {
            List<ItemStack> drops = event.getDrops();

            drops.clear();
            drops.addAll(ListUtil.randomAmount(new ItemStack(Material.IRON_INGOT), 2, 4));

            if (random.nextDouble() < 0.1) {
                drops.add(itemManager.getCustomItemStack(ItemIdentifier.RECURVE_BOW));
            }
        }
    }
}
