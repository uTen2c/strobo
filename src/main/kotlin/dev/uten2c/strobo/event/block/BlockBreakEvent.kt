package dev.uten2c.strobo.event.block

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity

class BlockBreakEvent(val state: BlockState, val player: ServerPlayerEntity) : CancellableEvent()