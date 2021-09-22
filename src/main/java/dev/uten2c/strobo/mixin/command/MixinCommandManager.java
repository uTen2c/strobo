package dev.uten2c.strobo.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.uten2c.strobo.Strobo;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class MixinCommandManager {

    @Shadow
    @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;

    // コマンド登録用関数の呼び出し
    @SuppressWarnings("deprecation")
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), remap = false)
    private void addCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        Strobo.registerCommand(dispatcher);
    }

    // デバッグ出力をいつでも有効化させる
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z"))
    private boolean setAlwaysTrue(Logger logger) {
        return true;
    }
}
