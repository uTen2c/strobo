package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーがキー(初期はF)を押してアイテムをオフハンドに持とうとしたときに呼び出される
 * @param player プレイヤー
 * @param mainHandStack メインハンドに持っている[ItemStack]
 * @param offHandStack オフハンドに持っている[ItemStack]
 */
class PlayerSwapHandItemsEvent(val player: ServerPlayerEntity, val mainHandStack: ItemStack, val offHandStack: ItemStack) : CancellableEvent()
