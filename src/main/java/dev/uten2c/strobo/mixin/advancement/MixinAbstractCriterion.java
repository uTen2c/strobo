package dev.uten2c.strobo.mixin.advancement;

import dev.uten2c.strobo.Strobo;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(AbstractCriterion.class)
public class MixinAbstractCriterion {
    @Inject(method = "trigger", at = @At("HEAD"), cancellable = true)
    private void trigger(ServerPlayerEntity player, Predicate<?> predicate, CallbackInfo ci) {
        if (Strobo.options.disableAdvancements) {
            ci.cancel();
        }
    }
}
