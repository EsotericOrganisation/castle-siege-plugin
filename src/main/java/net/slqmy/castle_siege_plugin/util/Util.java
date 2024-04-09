package net.slqmy.castle_siege_plugin.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class Util {
    private static final CastleSiegePlugin plugin = CastleSiegePlugin.getInstance();
    public static List<Location> toLocations(List<Double[]> xyzs, World world) {
        return xyzs.stream().map(xyz -> toLocation(xyz, world)).toList();
    }

    public static Location toLocation(Double[] xyz, World world) {
        return new Location(world, xyz[0], xyz[1], xyz[2]);
    }

    public static ServerLevel toNMSWorld(World world) {
        try {
            return (ServerLevel) world.getClass().getMethod("getHandle").invoke(world);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack toNMSItemStack(org.bukkit.inventory.ItemStack itemStack) {

        try {
            return (ItemStack) itemStack
                .getClass()
                .getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class)
                .invoke(null, itemStack);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static LivingEntity toNMSLivingEntity(org.bukkit.entity.LivingEntity livingEntity) {
        try {
            return (LivingEntity) livingEntity
                .getClass()
                .getMethod("getHandle")
                .invoke(livingEntity);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
