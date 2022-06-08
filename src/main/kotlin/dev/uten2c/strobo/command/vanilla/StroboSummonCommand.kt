package dev.uten2c.strobo.command.vanilla

import dev.uten2c.strobo.command.registerCommand
import net.minecraft.entity.EntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.SummonCommand
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.registry.Registry

internal object StroboSummonCommand {
    @JvmStatic
    fun register() {
        registerCommand("summon") {
            requires(2)
            identifier { getEntity ->
                suggests { _, builder ->
                    val str = builder.input.substring(builder.start)
                    Registry.ENTITY_TYPE
                        .map { it to Registry.ENTITY_TYPE.getId(it) }
                        .sortedBy { it.second }
                        .filter { (_, id) ->
                            id.namespace.startsWith(str) || id.path.startsWith(str) || id.toString().startsWith(str)
                        }
                        .forEach { (type, id) ->
                            builder.suggest(
                                if (id.path == Identifier.DEFAULT_NAMESPACE) id.path else id.toString(),
                                Text.translatable(Util.createTranslationKey("entity", EntityType.getId(type))),
                            )
                        }
                    return@suggests builder.buildFuture()
                }
                executes {
                    SummonCommand.execute(source, getEntity(), source.position, NbtCompound(), true)
                }

                vec3(true) { getPos ->
                    executes {
                        SummonCommand.execute(source, getEntity(), getPos(), NbtCompound(), true)
                    }

                    nbtCompound { getNbt ->
                        executes {
                            SummonCommand.execute(source, getEntity(), getPos(), getNbt(), false)
                        }
                    }
                }
            }
        }
    }
}
