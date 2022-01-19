package dev.uten2c.strobo.mixin;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistrySyncManager.class)
public class MixinRegistrySyncManager {

    // FabricAPIのレジストリー同期機能を無効化
    @Inject(method = "sendPacket(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void disable(MinecraftServer server, ServerPlayerEntity player, CallbackInfo ci) {
        ci.cancel();
    }
}
