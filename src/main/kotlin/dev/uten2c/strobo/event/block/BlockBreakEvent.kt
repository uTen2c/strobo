package dev.uten2c.strobo.event.block

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

class BlockBreakEvent(val state: BlockState, val pos: BlockPos, val player: ServerPlayerEntity) : CancellableEvent()