package dev.uten2c.strobo.mixin.stat;

import com.mojang.datafixers.DataFixer;
import dev.uten2c.strobo.Strobo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatHandler.class)
public abstract class MixinServerStatHandler {
    @Shadow
    public abstract void parse(DataFixer dataFixer, String json);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/ServerStatHandler;parse(Lcom/mojang/datafixers/DataFixer;Ljava/lang/String;)V"))
    private void parse(ServerStatHandler instance, DataFixer dataFixer, String json) {
        if (Strobo.options.disableStats) {
            return;
        }
        parse(dataFixer, json);
    }

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void save(CallbackInfo ci) {
        if (Strobo.options.disableStats) {
            ci.cancel();
        }
    }

    @Inject(method = "setStat", at = @At("HEAD"), cancellable = true)
    private void setStat(PlayerEntity player, Stat<?> stat, int value, CallbackInfo ci) {
        if (Strobo.options.disableStats) {
            ci.cancel();
        }
    }

    @Inject(method = "sendStats", at = @At("HEAD"), cancellable = true)
    private void sendStats(ServerPlayerEntity player, CallbackInfo ci) {
        if (Strobo.options.disableStats) {
            ci.cancel();
        }
    }
}
