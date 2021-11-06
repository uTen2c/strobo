package dev.uten2c.strobo.mixin.playerlist;

import dev.uten2c.strobo.util.ServerPlayerEntityKt;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void swapPlayerListName(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(ServerPlayerEntityKt.getListName((ServerPlayerEntity) (Object) this));
    }
}
