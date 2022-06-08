package dev.uten2c.strobo.command

import com.mojang.brigadier.context.CommandContext
import dev.uten2c.strobo.command.argument.ArgumentGetter
import dev.uten2c.strobo.command.argument.ScoreHoldersArgument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import java.util.function.Supplier

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CommandContext(private val context: CommandContext<ServerCommandSource>) {

    val source: ServerCommandSource = context.source
    val world: ServerWorld get() = source.world
    val playerOrThrow: ServerPlayerEntity get() = source.playerOrThrow

    @Deprecated("yarnマッピングの名前が変更された", ReplaceWith("playerOrThrow"))
    val player: ServerPlayerEntity get() = playerOrThrow

    fun sendFeedback(message: Text, broadcastToOps: Boolean = false) = source.sendFeedback(message, broadcastToOps)

    fun sendError(message: Text) = source.sendError(message)

    operator fun <T> ArgumentGetter<T>.invoke(): T = factory(context)

    operator fun ScoreHoldersArgument.invoke(
        supplier: Supplier<Collection<String>> = Supplier { emptyList() },
    ): Collection<String> = factory(context, supplier)
}
