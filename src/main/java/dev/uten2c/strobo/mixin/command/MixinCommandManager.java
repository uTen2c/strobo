package dev.uten2c.strobo.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.uten2c.strobo.Strobo;
import dev.uten2c.strobo.command.vanilla.StroboGiveCommand;
import dev.uten2c.strobo.command.vanilla.StroboSummonCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
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
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), remap = false)
    private void addCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        dev.uten2c.strobo.command.CommandManager.registerCommand(dispatcher);
    }

    // デバッグ出力をいつでも有効化させる
    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z", remap = false))
    private boolean setAlwaysTrue(Logger logger) {
        return true;
    }

    // バニラのgiveコマンドを置き換え
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/GiveCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"))
    private void replaceGiveCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (Strobo.replaceGiveCommand) {
            StroboGiveCommand.register();
        } else {
            GiveCommand.register(dispatcher);
        }
    }

    // バニラのsummonコマンドを置き換え
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/SummonCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"))
    private void replaceSummonCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (Strobo.replaceSummonCommand) {
            StroboSummonCommand.register();
        } else {
            SummonCommand.register(dispatcher);
        }
    }
}
