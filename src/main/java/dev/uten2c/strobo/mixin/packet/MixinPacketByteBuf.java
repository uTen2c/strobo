package dev.uten2c.strobo.mixin.packet;

import dev.uten2c.strobo.util.UuidHolder;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf implements UuidHolder {
    private UUID strobo$uuid;

    @Nullable
    @Override
    public UUID getUuid() {
        return strobo$uuid;
    }

    @Override
    public void setUuid(@Nullable UUID uuid) {
        strobo$uuid = uuid;
    }
}
