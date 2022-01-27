package dev.uten2c.strobo.command.argument

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

class ArgumentGetter<T>(internal val factory: (CommandContext<ServerCommandSource>) -> T)
