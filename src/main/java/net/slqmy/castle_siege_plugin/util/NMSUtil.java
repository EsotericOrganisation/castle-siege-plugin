package net.slqmy.castle_siege_plugin.util;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class NMSUtil {
    private NMSUtil() {}

    public static ServerLevel toNMSWorld(World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static ItemStack toNMSItemStack(org.bukkit.inventory.ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    public static LivingEntity toNMSLivingEntity(org.bukkit.entity.LivingEntity livingEntity) {
        return ((CraftLivingEntity) livingEntity).getHandle();
    }

    public static Entity toNMSEntity(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }

    public static ServerPlayer toNMSPlayer(Player player) {
        return (ServerPlayer) toNMSEntity(player);
    }

    @SuppressWarnings(value = "ConstantConditions")
    public static void sendEntityAddAndDataPackets(Entity entity, List<? extends Player> bukkitPlayers) {
        for (Player player : bukkitPlayers) {
            ServerGamePacketListenerImpl connection = toNMSPlayer(player).connection;

            List<SynchedEntityData.DataValue<?>> data = entity.getEntityData().packAll();

            connection.send(new ClientboundAddEntityPacket(entity));
            connection.send(new ClientboundSetEntityDataPacket(entity.getId(), data));
        }
    }

    public static void sendEntityRemovePackets(List<? extends Player> bukkitPlayers, Entity... entities) {
        for (Player player : bukkitPlayers) {
            ServerGamePacketListenerImpl connection = toNMSPlayer(player).connection;

            connection.send(new ClientboundRemoveEntitiesPacket(Arrays.stream(entities).mapToInt(Entity::getId).toArray()));
        }
    }
}
