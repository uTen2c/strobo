package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.ItemEntity
import net.minecraft.server.network.ServerPlayerEntity

class PlayerDropItemEvent(val player: ServerPlayerEntity, var item: ItemEntity) : CancellableEvent()