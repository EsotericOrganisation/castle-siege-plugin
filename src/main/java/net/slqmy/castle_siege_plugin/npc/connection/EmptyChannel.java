package net.slqmy.castle_siege_plugin.npc.connection;
import io.netty.channel.*;

import java.net.SocketAddress;

public class EmptyChannel extends AbstractChannel {
    private final ChannelConfig config = new DefaultChannelConfig(this);

    public EmptyChannel(Channel parent) {
        super(parent);
    }

    @Override
    public ChannelConfig config() {
        config.setAutoRead(true);
        return config;
    }

    @Override
    protected void doBeginRead() {}

    @Override
    protected void doBind(SocketAddress arg0) {}

    @Override
    protected void doClose() {}

    @Override
    protected void doDisconnect() {}

    @Override
    protected void doWrite(ChannelOutboundBuffer arg0) {}

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected boolean isCompatible(EventLoop arg0) {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    protected SocketAddress localAddress0() {
        return null;
    }

    @Override
    public ChannelMetadata metadata() {
        return new ChannelMetadata(true);
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }
}
