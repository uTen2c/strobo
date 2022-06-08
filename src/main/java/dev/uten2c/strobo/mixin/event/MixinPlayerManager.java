package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.player.PlayerJoinEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    private ServerPlayerEntity player;

    private ClientConnection connection;

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.player = player;
        this.connection = connection;
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private void onJoin(PlayerManager playerManager, Text message, RegistryKey<MessageType> typeKey) {
        var event = new PlayerJoinEvent(player, connection, message);
        event.callEvent();
        playerManager.broadcast(event.getMessage(), typeKey);
    }
}
