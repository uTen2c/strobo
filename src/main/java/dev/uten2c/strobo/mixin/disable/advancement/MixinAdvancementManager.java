package dev.uten2c.strobo.mixin.disable.advancement;

import dev.uten2c.strobo.Strobo;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AdvancementManager.class)
public class MixinAdvancementManager {
    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private void load(Map<Identifier, Advancement.Builder> map, CallbackInfo ci) {
        if (Strobo.options.disableAdvancements) {
            ci.cancel();
        }
    }
}
