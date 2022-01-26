package dev.uten2c.strobo

import com.mojang.brigadier.CommandDispatcher
import dev.uten2c.strobo.command.Command
import dev.uten2c.strobo.command.CommandBuilder
import dev.uten2c.strobo.command.CommandContext
import dev.uten2c.strobo.event.Event
import dev.uten2c.strobo.event.EventListener
import dev.uten2c.strobo.task.TaskRunner
import net.fabricmc.api.ModInitializer
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

object Strobo : ModInitializer {
    @JvmField
    var replaceGiveCommand = true

    @JvmField
    var replaceSummonCommand = true

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
        commands.forEach { (name, builder) ->
            val argumentBuilder = CommandManager.literal(name)
            val commandBuilder = CommandBuilder(argumentBuilder)
            builder(commandBuilder)
            val node = dispatcher.register(argumentBuilder)
            commandBuilder.aliases?.forEach { alias ->
                val aliasNode = CommandManager.literal(alias)
                val filter = commandBuilder.filter
                if (filter != null) {
                    aliasNode.requires { filter(it) }
                }
                val executes = commandBuilder.executes
                if (executes != null) {
                    aliasNode.executes {
                        executes(CommandContext(it))
                        return@executes 0
                    }
                }
                dispatcher.register(aliasNode.redirect(node))
            }
        }
    }
}
