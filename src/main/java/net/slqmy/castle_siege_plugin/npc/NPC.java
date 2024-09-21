package net.slqmy.castle_siege_plugin.npc;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.game.data.npc.NPCData;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import net.slqmy.castle_siege_plugin.util.interfaces.ITickable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.List;
import java.util.function.Consumer;

@Getter
public final class NPC implements ITickable {
    private final CastleSiegePlugin plugin;
    private final ServerPlayer handle;
    private final NPCData data;
    private net.minecraft.world.entity.Display.TextDisplay nameTag;

    @Setter
    private List<? extends Player> audience;

    @Setter
    private Consumer<Player> onInteract;

    @Setter
    private Runnable onTick;

    public NPC(ServerPlayer npc, NPCData data) {
        this.plugin = CastleSiegePlugin.getInstance();
        this.handle = npc;
        this.data = data;
    }

    public void unregister() {
        NMSUtil.sendEntityRemovePackets(audience, handle, nameTag);
    }

    public int getId() {
        return handle.getId();
    }

    public Player asBukkitPlayer() {
        return handle.getBukkitEntity();
    }

    public void setCustomName(Component component) {
        this.nameTag = new net.minecraft.world.entity.Display.TextDisplay(
            net.minecraft.world.entity.EntityType.TEXT_DISPLAY, handle.level());

        TextDisplay bukkitNameTag = (TextDisplay) nameTag.getBukkitEntity();

        bukkitNameTag.text(component);
        bukkitNameTag.setAlignment(TextDisplay.TextAlignment.CENTER);
        bukkitNameTag.setBillboard(Display.Billboard.VERTICAL);

        nameTag.setPos(handle.getX(), handle.getY() + 2.0, handle.getZ());
    }

    public void setLocation(Location location) {
        handle.setPos(location.getX(), location.getY(), location.getZ());
    }

    public void setYawAndPitch(float yaw, float pitch) {
        handle.setYRot(yaw);
        handle.setXRot(pitch);
    }

    public void sendPacketsToPlayers() {
        audience.forEach(this::sendPacketsToPlayer);
        audience.forEach(this::sendRotationPacketsToPlayer);

        NMSUtil.sendEntityAddAndDataPackets(handle, audience);
        NMSUtil.sendEntityAddAndDataPackets(nameTag, audience);
    }

    public void sendRotationPacketsToPlayers() {
        audience.forEach(this::sendRotationPacketsToPlayer);
    }

    public void onInteract(Player player) {
        onInteract.accept(player);
    }

    @Override
    public void tick() {
        onTick.run();
    }

    private void sendPacketsToPlayer(Player player) {
        ServerGamePacketListenerImpl connection = NMSUtil.toNMSPlayer(player).connection;

        connection.send(
            ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(handle)));
        Bukkit.getScheduler().runTaskLater(plugin, () ->
            connection.send(new ClientboundPlayerInfoRemovePacket(List.of(handle.getUUID()))), 20L);
    }

    private void sendRotationPacketsToPlayer(Player player) {
        ServerGamePacketListenerImpl connection = NMSUtil.toNMSPlayer(player).connection;

        connection.send(new ClientboundRotateHeadPacket(handle, (byte) (handle.getYRot() * 256.0F / 360.0F)));
        connection.send(new ClientboundMoveEntityPacket.Rot(
            getId(),
            (byte) (handle.getYRot() * 256 / 360),
            (byte) (handle.getXRot() * 256 / 360),
            handle.onGround()));
    }
}
