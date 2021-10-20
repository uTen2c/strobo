package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーがスペクテイターモードでエンティティに乗り移るときに呼び出される
 * @param player プレイヤー
 * @param currentSpectatorTarget 現在乗り移っているエンティティ
 * @param newSpectatorTarget 乗り移ろうとしているエンティティ
 */
class PlayerStartSpectatingEntityEvent(val player: ServerPlayerEntity, val currentSpectatorTarget: Entity, val newSpectatorTarget: Entity) : CancellableEvent()
