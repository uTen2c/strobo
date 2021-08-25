package dev.uten2c.strobo.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

data class Command(val builder: LiteralArgumentBuilder<ServerCommandSource>)
