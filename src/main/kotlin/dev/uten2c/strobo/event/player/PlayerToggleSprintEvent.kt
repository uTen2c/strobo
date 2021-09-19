package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity

class PlayerToggleSprintEvent(val player: ServerPlayerEntity, val isSprinting: Boolean) : Event()