package dev.uten2c.strobo.util;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static dev.uten2c.strobo.VariabelsKt.server;

public interface UuidHolder {
    @Nullable UUID getUuid();

    void setUuid(@Nullable UUID uuid);

    default @Nullable ServerPlayerEntity getPlayerOrNull() {
        return server.getPlayerManager().getPlayer(getUuid());
    }
}
