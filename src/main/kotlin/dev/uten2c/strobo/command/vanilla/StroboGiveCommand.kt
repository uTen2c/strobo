package dev.uten2c.strobo.command.vanilla

import dev.uten2c.strobo.command.registerCommand
import net.minecraft.server.command.GiveCommand
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

internal object StroboGiveCommand {
    @JvmStatic
    fun register() {
        registerCommand("give") {
            requires(2)
            players("targets") {
                itemStack("item") {
                    suggests { _, builder ->
                        val str = builder.input.substring(builder.start)
                        Registry.ITEM
                            .map { Registry.ITEM.getId(it) }
                            .sorted()
                            .filter { id ->
                                id.namespace.startsWith(str) || id.path.startsWith(str) || id.toString().startsWith(str)
                            }
                            .forEach { id ->
                                builder.suggest(if (id.path == Identifier.DEFAULT_NAMESPACE) id.path else id.toString())
                            }
                        return@suggests builder.buildFuture()
                    }
                    executes {
                        GiveCommand.execute(source, getItemStack("item"), getPlayers("targets"), 1)
                    }

                    integer("count", 1) {
                        executes {
                            GiveCommand.execute(
                                source,
                                getItemStack("item"),
                                getPlayers("targets"),
                                getInteger("count"),
                            )
                        }
                    }
                }
            }
        }
    }
}
