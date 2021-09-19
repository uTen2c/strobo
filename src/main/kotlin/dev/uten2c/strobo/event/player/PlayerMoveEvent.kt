package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import dev.uten2c.strobo.util.Location
import net.minecraft.server.network.ServerPlayerEntity

class PlayerMoveEvent(val player: ServerPlayerEntity, val from: Location, val to: Location) : CancellableEvent()