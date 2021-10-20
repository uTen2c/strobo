package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

/**
 * イベントを聞く
 * @param handler イベントハンドラー
 * @return イベントリスナー
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> listenEvent(noinline handler: (T) -> Unit): EventListener =
    internalListenEvent(T::class, handler as (Event) -> Unit)

@ApiStatus.Internal
fun internalListenEvent(clazz: KClass<out Event>, handler: (Event) -> Unit): EventListener {
    val eventListener = EventListener(clazz, handler)
    Strobo.eventListeners[clazz] =
        Strobo.eventListeners.getOrDefault(clazz, HashSet()).apply {
            add(eventListener)
        }
    return eventListener
}
