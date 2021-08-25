package dev.uten2c.strobo.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.uten2c.strobo.Strobo
import net.minecraft.server.command.ServerCommandSource

/**
 * コマンドを登録する
 * @param name コマンド名
 * @param builder コマンドの動作
 */
fun registerCommand(name: String, builder: CommandBuilder.() -> Unit) {
    val arg = LiteralArgumentBuilder.literal<ServerCommandSource>(name)
    builder(CommandBuilder(arg))
    Strobo.commands.add(Command(arg))
}
