package dev.uten2c.strobo.event

import kotlin.reflect.KClass

data class EventListener(val clazz: KClass<out Event>, val handler: (Event) -> Unit, val priority: EventPriority) {

    internal var unlistened = false

    /**
     * イベントリスナーを消す
     */
    fun unlisten() {
        unlistened = true
    }
}
