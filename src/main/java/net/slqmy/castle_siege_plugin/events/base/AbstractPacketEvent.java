package net.slqmy.castle_siege_plugin.events.base;

import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractPacketEvent<P extends Packet<? extends ServerGamePacketListener>> {
    protected final CastleSiegePlugin plugin;

    @Getter
    protected int priority;

    public AbstractPacketEvent() {
        this.plugin = CastleSiegePlugin.getInstance();
    }

    public void register() {
        plugin.getPacketInterceptor()
            .registerListener(this);
    }

    public P castPacket(Packet<?> packet) {
        return getPacketClass().cast(packet);
    }

    public void onPacketReceive(Player player, Object packet) {
        onPacketReceive(player, castPacket((Packet<?>) packet));
    }

    public boolean validatePacket(Object packet) {
        return validatePacket(castPacket((Packet<?>) packet));
    }

    public List<Class<? extends Packet<?>>> getIgnoredPackets() {
        return List.of();
    }

    public abstract Class<P> getPacketClass();
    public abstract boolean validatePacket(P packet);
    public abstract void onPacketReceive(Player player, P packet);
}
