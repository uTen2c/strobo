package dev.uten2c.strobo.mixin.screen;

import com.mojang.authlib.GameProfile;
import dev.uten2c.strobo.screen.StroboScreenHandlerListener;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Mutable
    @Shadow
    @Final
    private ScreenHandlerListener screenHandlerListener;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createFilterer(Lnet/minecraft/server/network/ServerPlayerEntity;)Lnet/minecraft/server/filter/TextStream;"))
    private void swapScreenHandlerListener(MinecraftServer server, ServerWorld world, GameProfile profile, PlayerPublicKey publicKey, CallbackInfo ci) {
        screenHandlerListener = new StroboScreenHandlerListener(
                (ServerPlayerEntity) (Object) MixinServerPlayerEntity.this,
                screenHandlerListener
        );
    }
}
