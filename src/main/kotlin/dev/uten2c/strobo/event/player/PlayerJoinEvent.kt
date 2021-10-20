package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.network.ClientConnection
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

/**
 * プレイヤーがサーバーに入ったときに呼び出される
 * @param player プレイヤー
 * @param connection ClientConnection
 * @param message メッセージ
 */
class PlayerJoinEvent(val player: ServerPlayerEntity, val connection: ClientConnection, var message: Text) : Event()
