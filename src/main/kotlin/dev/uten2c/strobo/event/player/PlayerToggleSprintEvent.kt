package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーが歩行/ダッシュを切り替えたときに呼び出される
 * @param player プレイヤー
 * @param isSprinting ダッシュしているか
 */
class PlayerToggleSprintEvent(val player: ServerPlayerEntity, val isSprinting: Boolean) : Event()
