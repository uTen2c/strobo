package dev.uten2c.strobo.event.block

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

/**
 * ブロック破壊時に呼び出される
 * @param state BlockState
 * @param pos 座標
 * @param player 破壊したプレイヤー
 */
class BlockBreakEvent(val state: BlockState, val pos: BlockPos, val player: ServerPlayerEntity) : CancellableEvent()
