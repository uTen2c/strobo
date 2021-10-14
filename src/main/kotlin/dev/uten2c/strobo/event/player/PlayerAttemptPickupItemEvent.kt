package dev.uten2c.strobo.event.player

import dev.uten2c.strobo.event.CancellableEvent
import net.minecraft.entity.ItemEntity
import net.minecraft.server.network.ServerPlayerEntity

class PlayerAttemptPickupItemEvent(val player: ServerPlayerEntity, val entity: ItemEntity, val remaining: Int) : CancellableEvent() {

    /**
     * アイテムがプレイヤーに向かって飛ぶかどうかの設定
     */
    var flyAtPlayer: Boolean = true

    override var isCancelled: Boolean
        get() = super.isCancelled
        set(value) {
            super.isCancelled = value
            flyAtPlayer = !value
        }
}
