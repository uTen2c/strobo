package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo

/**
 * イベントクラス
 */
abstract class Event {

    /**
     * イベントを呼び出す
     * @return キャンセルされたらfalseが帰る
     */
    fun callEvent(): Boolean {
        val set = Strobo.eventListeners.getOrDefault(this::class, HashSet())
        set.forEach { it.handler(this) }
        set.removeAll(removeHandlers)
        Strobo.eventListeners[this::class] = set
        return if (this is CancellableEvent) !isCancelled else true
    }

    companion object {
        internal val removeHandlers = HashSet<EventListener>()
    }
}
