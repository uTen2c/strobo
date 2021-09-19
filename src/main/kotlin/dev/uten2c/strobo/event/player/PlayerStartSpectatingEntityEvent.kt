package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

class PlayerStartSpectatingEntityEvent(val player: ServerPlayerEntity, val currentSpectatorTarget: Entity, val newSpectatorTarget: Entity) : CancellableEvent()