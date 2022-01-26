package dev.uten2c.strobo.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.CommandNode
import dev.uten2c.strobo.Strobo
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.jetbrains.annotations.ApiStatus

internal object CommandManager {
    private val commands = HashSet<Command>()
    private val childrenField = CommandNode::class.java.getDeclaredField("children").apply {
        isAccessible = true
    }
    private val literalsField = CommandNode::class.java.getDeclaredField("literals").apply {
        isAccessible = true
    }

    internal fun addCommand(command: Command) {
        commands.add(command)
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

    internal fun reloadCommands() {
        val commandManager = Strobo.server.commandManager
        val dispatcher = commandManager.dispatcher
        commands.forEach { command ->
            val root = dispatcher.root
            (childrenField.get(root) as MutableMap<*, *>).remove(command.name)
            (literalsField.get(root) as MutableMap<*, *>).remove(command.name)
        }
        registerCommand(dispatcher)
        sendCommandTree()
    }

    private fun sendCommandTree() {
        Strobo.server.playerManager.playerList.forEach(Strobo.server.commandManager::sendCommandTree)
    }
}
