package net.slqmy.castle_siege_plugin.util.pdc;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class UUIDTagType implements PersistentDataType<byte[], UUID> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(complex.getMostSignificantBits());
        buffer.putLong(complex.getLeastSignificantBits());
        return buffer.array();
    }

    @Override
    public @NotNull UUID fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(primitive);
        long firstLong = buffer.getLong();
        long secondLong = buffer.getLong();
        return new UUID(firstLong, secondLong);
    }
}
