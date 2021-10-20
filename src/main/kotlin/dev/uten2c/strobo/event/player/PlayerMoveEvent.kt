package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import dev.uten2c.strobo.util.Location
import net.minecraft.server.network.ServerPlayerEntity

/**
 * プレイヤーが移動もしくは視点移動したときに呼び出される
 * @param player プレイヤー
 * @param from 移動前の座標
 * @param to 移動後の座標
 */
class PlayerMoveEvent(val player: ServerPlayerEntity, val from: Location, val to: Location) : CancellableEvent() {

    val changesPosition: Boolean get() = from.x != to.x || from.y != to.y || from.z != to.z

    val changesLook: Boolean get() = from.yaw != to.yaw || from.pitch != to.pitch
}
