package dev.uten2c.strobo

import com.mojang.brigadier.CommandDispatcher
import dev.uten2c.strobo.command.Command
import dev.uten2c.strobo.event.Event
import dev.uten2c.strobo.event.EventListener
import dev.uten2c.strobo.task.TaskRunner
import net.fabricmc.api.ModInitializer
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

object Strobo : ModInitializer {

    internal val commands = HashSet<Command>()
    internal val eventListeners = HashMap<KClass<out Event>, HashSet<EventListener>>()
    lateinit var server: MinecraftServer
        internal set

    override fun onInitialize() {
        GlobalListeners.setup()
        TaskRunner.setup()
    }

    @JvmStatic
    @ApiStatus.Internal
    fun registerCommand(dispatcher: CommandDispatcher<ServerCommandSource>) {
        commands.forEach {
            dispatcher.register(it.builder)
        }
    }
}
