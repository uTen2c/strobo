package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/**
 * プレイヤーがサーバーから出たときに呼び出される
 * @param player プレイヤー
 * @param message メッセージ
 */
class PlayerQuitEvent(val player: ServerPlayerEntity, var message: Text) : Event()
