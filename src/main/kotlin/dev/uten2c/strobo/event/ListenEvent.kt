package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KClass

/**
 * イベントを聞く
 * @param priority イベント優先度
 * @param handler イベントハンドラー
 * @return イベントリスナー
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> listenEvent(priority: EventPriority = EventPriority.NORMAL, noinline handler: (T) -> Unit): EventListener =
    internalListenEvent(T::class, handler as (Event) -> Unit, priority)

@ApiStatus.Internal
fun internalListenEvent(clazz: KClass<out Event>, handler: (Event) -> Unit, priority: EventPriority): EventListener {
    val eventListener = EventListener(clazz, handler, priority)
    Strobo.eventListeners[clazz] = Strobo.eventListeners.getOrDefault(clazz, HashSet()).apply {
        add(eventListener)
    }
    return eventListener
}
