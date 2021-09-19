package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity

class PlayerItemHeldEvent(val player: ServerPlayerEntity, val prev: Int, val new: Int) : Event()