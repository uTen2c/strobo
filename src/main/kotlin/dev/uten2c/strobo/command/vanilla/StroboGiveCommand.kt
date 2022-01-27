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
            players { getTargets ->
                itemStack { getItem ->
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
                        GiveCommand.execute(source, getItem(), getTargets(), 1)
                    }

                    integer(1) { getCount ->
                        executes {
                            GiveCommand.execute(source, getItem(), getTargets(), getCount())
                        }
                    }
                }
            }
        }
    }
}
