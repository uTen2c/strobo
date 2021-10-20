package dev.uten2c.strobo.event

import kotlin.reflect.KClass

data class EventListener(val clazz: KClass<out Event>, val handler: (Event) -> Unit) {

    /**
     * イベントリスナーを消す
     */
    fun unlisten() {
        Event.removeHandlers.add(this)
    }
}
