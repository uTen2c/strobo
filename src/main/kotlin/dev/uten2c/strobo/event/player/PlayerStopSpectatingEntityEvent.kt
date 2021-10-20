package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーがスペクテイターモードのときに乗り移るのをやめようとしたときに呼び出される
 * @param player プレイヤー
 * @param spectatorTarget 現在乗り移っているエンティティ
 */
class PlayerStopSpectatingEntityEvent(val player: ServerPlayerEntity, val spectatorTarget: Entity) : CancellableEvent()
