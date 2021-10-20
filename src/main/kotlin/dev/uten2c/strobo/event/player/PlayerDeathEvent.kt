package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.Event
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーが死亡したときに呼び出される
 * @param player プレイヤー
 */
class PlayerDeathEvent(val player: ServerPlayerEntity) : Event()
