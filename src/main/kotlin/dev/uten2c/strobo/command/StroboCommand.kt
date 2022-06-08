package dev.uten2c.strobo.command

import dev.uten2c.strobo.Strobo
import dev.uten2c.strobo.util.color
import dev.uten2c.strobo.util.emptyText
import dev.uten2c.strobo.util.text
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import net.minecraft.util.Formatting

internal object StroboCommand {
    private const val COMMAND_RELOADED_MESSAGE = "Command reloaded."
    private val version = FabricLoader.getInstance().getModContainer("strobo").get().metadata.version.friendlyString

    fun register() {
        registerCommand("strobo") {
            literal("reload") {
                requires(2)
                executes {
                    CommandManager.reloadCommands()
                    sendStroboMessage(text(COMMAND_RELOADED_MESSAGE))
                    Strobo.logger.info(COMMAND_RELOADED_MESSAGE)
                }
            }
            literal("version") {
                executes {
                    val text = emptyText()
                        .append("v")
                        .append(text(version).color(Formatting.GOLD))
                    sendStroboMessage(text)
                }
            }
        }
    }

    private fun CommandContext.sendStroboMessage(message: Text) {
        if (source.output.shouldReceiveFeedback() && !source.silent) {
            val text = emptyText().color(Formatting.GRAY)
            text.append(text("Strobo").color(Formatting.GOLD))
            text.append(text(" » ").color(Formatting.DARK_GRAY))
            text.append(message)
            source.output.sendMessage(text)
        }
    }
}
