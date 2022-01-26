package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

/**
 * 手に持っているアイテムを落とすときに呼び出されます
 * @param player プレイヤー
 * @param stack 落とされる予定のアイテム
 * @param entireStack アイテムをすべて落とそうとしたか
 */
class PlayerDropSelectedItemEvent(
    val player: ServerPlayerEntity,
    val stack: ItemStack,
    val entireStack: Boolean,
) : CancellableEvent()
