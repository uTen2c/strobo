package dev.uten2c.strobo.mixin.disable.advancement;

import dev.uten2c.strobo.Strobo;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.ServerAdvancementLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerAdvancementTracker.class)
public abstract class MixinPlayerAdvancementTracker {
    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void load(ServerAdvancementLoader advancementLoader, CallbackInfo ci) {
        if (Strobo.options.disableAdvancements) {
            ci.cancel();
        }
    }

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void save(CallbackInfo ci) {
        if (Strobo.options.disableAdvancements) {
            ci.cancel();
        }
    }
}
