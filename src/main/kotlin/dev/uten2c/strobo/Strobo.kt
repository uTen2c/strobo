package dev.uten2c.strobo

import com.mojang.brigadier.CommandDispatcher
import dev.uten2c.strobo.event.Event
import dev.uten2c.strobo.command.Command
import dev.uten2c.strobo.event.EventListener
import net.fabricmc.api.ModInitializer
import net.minecraft.server.command.ServerCommandSource
import kotlin.reflect.KClass

object Strobo : ModInitializer {

    internal val commands = HashSet<Command>()
    internal val eventListeners = HashMap<KClass<out Event>, HashSet<EventListener>>()

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