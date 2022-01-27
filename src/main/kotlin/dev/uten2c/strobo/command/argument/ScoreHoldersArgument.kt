package dev.uten2c.strobo.command.argument

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import java.util.function.Supplier

class ScoreHoldersArgument(
    internal val factory: (CommandContext<ServerCommandSource>, Supplier<Collection<String>>) -> Collection<String>,
)
