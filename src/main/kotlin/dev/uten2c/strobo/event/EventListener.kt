package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo
import kotlin.reflect.KClass

data class EventListener(val clazz: KClass<out Event>, val handler: (Event) -> Unit) {

    fun unlisten() {
        Strobo.eventListeners[clazz]?.remove(this)
    }
}