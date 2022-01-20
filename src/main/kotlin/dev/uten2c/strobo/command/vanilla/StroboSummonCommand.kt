package dev.uten2c.strobo.command.vanilla

import dev.uten2c.strobo.command.registerCommand
import net.minecraft.entity.EntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.SummonCommand
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.registry.Registry

object StroboSummonCommand {
    @JvmStatic
    fun register() {
        registerCommand("summon") {
            requires(2)
            identifier("entity") {
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
                                TranslatableText(Util.createTranslationKey("entity", EntityType.getId(type))),
                            )
                        }
                    return@suggests builder.buildFuture()
                }
                executes {
                    SummonCommand.execute(source, getIdentifier("entity"), source.position, NbtCompound(), true)
                }

                vec3("pos", true) {
                    executes {
                        SummonCommand.execute(source, getIdentifier("entity"), getVec3("pos"), NbtCompound(), true)
                    }

                    nbtCompound("nbt") {
                        executes {
                            val entity = getIdentifier("entity")
                            val pos = getVec3("pos")
                            val nbt = getNbtCompound("nbt")
                            SummonCommand.execute(source, entity, pos, nbt, false)
                        }
                    }
                }
            }
        }
    }
}
