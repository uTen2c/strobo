package dev.uten2c.strobo.mixin.event;

import dev.uten2c.strobo.event.server.ServerEndTickEvent;
import dev.uten2c.strobo.event.server.ServerStartTickEvent;
import dev.uten2c.strobo.event.server.ServerStartingEvent;
import dev.uten2c.strobo.event.server.ServerStoppingEvent;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"))
    private void beforeSetupServer(CallbackInfo ci) {
        new ServerStartingEvent((MinecraftServer) (Object) this).callEvent();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"))
    private void onStartTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        new ServerStartTickEvent((MinecraftServer) (Object) this).callEvent();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEndTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        new ServerEndTickEvent((MinecraftServer) (Object) this).callEvent();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void beforeShutdownServer(CallbackInfo info) {
        new ServerStoppingEvent((MinecraftServer) (Object) this).callEvent();
    }
}
