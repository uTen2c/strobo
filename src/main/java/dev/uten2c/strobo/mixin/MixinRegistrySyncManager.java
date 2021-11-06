package dev.uten2c.strobo.mixin;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistrySyncManager.class)
public class MixinRegistrySyncManager {

    // FabricAPIのレジストリー同期機能を無効化
    @Inject(method = "createPacket", at = @At("HEAD"), cancellable = true, remap = false)
    private static void disable(CallbackInfoReturnable<Packet<?>> cir) {
        cir.setReturnValue(null);
    }
}
