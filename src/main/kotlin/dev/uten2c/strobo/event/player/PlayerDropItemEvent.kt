package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.ItemEntity
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーがアイテムを落としたときに呼び出される
 * @param player プレイヤー
 * @param item アイテムエンティティ
 */
class PlayerDropItemEvent(val player: ServerPlayerEntity, var item: ItemEntity) : CancellableEvent()
