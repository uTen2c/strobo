package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST", "DEPRECATION")
inline fun <reified T : Event> listenEvent(noinline handler: (T) -> Unit): EventListener =
    internalListenEvent(T::class, handler as (Event) -> Unit)

@Deprecated("Internal API")
fun internalListenEvent(clazz: KClass<out Event>, handler: (Event) -> Unit): EventListener {
    val eventListener = EventListener(clazz, handler)
    Strobo.eventListeners[clazz] =
        Strobo.eventListeners.getOrDefault(clazz, HashSet()).apply {
            add(eventListener)
        }
    return eventListener
}
