package dev.uten2c.strobo.util;

import dev.uten2c.strobo.Strobo;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UuidHolder {
    @Nullable UUID getUuid();

    void setUuid(@Nullable UUID uuid);

    default @Nullable ServerPlayerEntity getPlayerOrNull() {
        return Strobo.server.getPlayerManager().getPlayer(getUuid());
    }
}
