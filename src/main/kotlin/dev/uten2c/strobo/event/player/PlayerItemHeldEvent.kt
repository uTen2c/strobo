package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーがホットバーのスロットを変更したときに呼び出される
 * @param player プレイヤー
 * @param prev 変更前のスロットID
 * @param new 変更後のスロットID
 */
class PlayerItemHeldEvent(val player: ServerPlayerEntity, val prev: Int, val new: Int) : Event()
