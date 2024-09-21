package net.slqmy.castle_siege_plugin.packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.slqmy.castle_siege_plugin.CastleSiegePlugin;
import net.slqmy.castle_siege_plugin.events.base.AbstractPacketEvent;
import net.slqmy.castle_siege_plugin.util.NMSUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PacketInterceptor {
    private final List<AbstractPacketEvent<? extends Packet<? extends ServerGamePacketListener>>> listeners;

    public PacketInterceptor() {
        this.listeners = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        ServerPlayer serverPlayer = NMSUtil.toNMSPlayer(player);

        ChannelPipeline pipeline = serverPlayer.connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", serverPlayer.getName().getString(), new PlayerChannelHandler(serverPlayer));
    }

    public void removePlayer(Player player) {
        ServerPlayer serverPlayer = NMSUtil.toNMSPlayer(player);

        Channel channel = serverPlayer.connection.connection.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(serverPlayer.getName().getString()));
    }

    public void registerListener(AbstractPacketEvent<? extends Packet<? extends ServerGamePacketListener>> event) {
        listeners.add(event);
        listeners.sort(Comparator.comparingInt(AbstractPacketEvent::getPriority));
    }

    private final class PlayerChannelHandler extends ChannelDuplexHandler {
        private final CastleSiegePlugin plugin;
        private final ServerPlayer player;

        private final List<Class<? extends Packet<?>>> ignoredPackets;

        public PlayerChannelHandler(ServerPlayer player) {
            this.plugin = CastleSiegePlugin.getInstance();
            this.player = player;

            this.ignoredPackets = new ArrayList<>();
        }

        @Override
        public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object packet) throws Exception {
            if (ignoredPackets.contains(packet.getClass())) {
                ignoredPackets.remove(packet.getClass());
                return;
            }

            for (AbstractPacketEvent<? extends Packet<? extends ServerGamePacketListener>> event : listeners) {
                if (event.getPacketClass().isInstance(packet)) {
                    if (!event.validatePacket(packet))
                        continue;

                    ignoredPackets.addAll(event.getIgnoredPackets());

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        event.onPacketReceive(player.getBukkitEntity(), packet);
                        ignoredPackets.removeAll(event.getIgnoredPackets());
                    });
                }
            }

            super.channelRead(ctx, packet);
        }
    }
}





