package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo

/**
 * イベントクラス
 */
abstract class Event {

    lateinit var eventListener: EventListener
        private set

    /**
     * イベントを呼び出す
     * @return キャンセルされたらfalseが帰る
     */
    fun callEvent(): Boolean {
        val set = Strobo.eventListeners.getOrDefault(this::class, HashSet())
        EventPriority.values().forEach { priority ->
            set.filter { it.priority == priority }
                .forEach {
                    try {
                        this.eventListener = it
                        it.handler(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
        set.removeAll(removeHandlers)
        removeHandlers.clear()
        Strobo.eventListeners[this::class] = set
        return if (this is CancellableEvent) !isCancelled else true
    }

    companion object {
        internal val removeHandlers = HashSet<EventListener>()
    }
}
