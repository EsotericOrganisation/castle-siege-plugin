package net.slqmy.castle_siege_plugin.events.packet;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.slqmy.castle_siege_plugin.events.base.AbstractPacketEvent;
import net.slqmy.castle_siege_plugin.game.data.player.TeamPlayer;
import net.slqmy.castle_siege_plugin.npc.NPC;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

import static net.slqmy.castle_siege_plugin.util.NMSUtil.toNMSPlayer;

public final class PlayerInteractPacketEvent extends AbstractPacketEvent<ServerboundInteractPacket> {
    public PlayerInteractPacketEvent() {
        super();
        this.priority = 0;
    }

    @Override
    public Class<ServerboundInteractPacket> getPacketClass() {
        return ServerboundInteractPacket.class;
    }

    @Override
    public List<Class<? extends Packet<?>>> getIgnoredPackets() {
        return List.of(ServerboundUseItemPacket.class);
    }

    @Override
    public boolean validatePacket(ServerboundInteractPacket packet) {
        try {
            Field action = packet.getClass().getDeclaredField("action");
            action.setAccessible(true);
            Object actionValue = action.get(packet);

            Class<?> interactAction = Class.forName("net.minecraft.network.protocol.game.ServerboundInteractPacket$InteractionAction");
            if (!interactAction.isInstance(actionValue)) return false;

            Field handField = interactAction.cast(actionValue).getClass().getDeclaredField("hand");
            handField.setAccessible(true);

            InteractionHand hand = (InteractionHand) handField.get(interactAction.cast(actionValue));
            if (hand != InteractionHand.MAIN_HAND) return false;
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            plugin.getLogger().warning("Failed to get action field from ServerboundInteractPacket: \n" + e.getMessage());
        }

        return true;
    }

    @Override
    public void onPacketReceive(Player player, ServerboundInteractPacket packet) {
        TeamPlayer teamPlayer = TeamPlayer.getFrom(player);
        if (teamPlayer == null) return;

        int entityId = packet.getEntityId();
        NPC npc = teamPlayer.getGame().getMapData().getNPCsById().get(entityId);

        if (npc == null) return;

        npc.onInteract(player);
        player.swingMainHand();

        ServerPlayer serverPlayer = toNMSPlayer(player);
        serverPlayer.stopUsingItem();
        serverPlayer.resyncUsingItem(serverPlayer);
    }
}
