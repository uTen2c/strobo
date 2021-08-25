package dev.uten2c.strobo

import com.mojang.brigadier.CommandDispatcher
import dev.uten2c.strobo.command.Command
import net.fabricmc.api.ModInitializer
import net.minecraft.server.command.ServerCommandSource

object Strobo : ModInitializer {

    internal val commands = HashSet<Command>()

    override fun onInitialize() {

    }

    @JvmStatic
    @Deprecated("Internal API", ReplaceWith(""))
    fun registerCommand(dispatcher: CommandDispatcher<ServerCommandSource>) {
        commands.forEach {
            dispatcher.register(it.builder)
        }
    }
}