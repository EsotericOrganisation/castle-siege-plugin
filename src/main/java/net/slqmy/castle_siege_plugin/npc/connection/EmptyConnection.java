package net.slqmy.castle_siege_plugin.npc.connection;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serial;
import java.net.SocketAddress;

public class EmptyConnection extends Connection {

    @SuppressWarnings(value = "ConstantConditions")
    public EmptyConnection(@Nullable PacketFlow flag) {
        super(flag);

        this.channel = new EmptyChannel(null);
        this.address = new SocketAddress() { @Serial private static final long serialVersionUID = 8207338859896320185L; };
    }

    @Override
    public void flushChannel() {}

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(@NotNull Packet packet) {}

    @Override
    public void send(@NotNull Packet packet, PacketSendListener listener) {}

    @Override
    public void send(@NotNull Packet packet, PacketSendListener listener, boolean flag) {}

}
