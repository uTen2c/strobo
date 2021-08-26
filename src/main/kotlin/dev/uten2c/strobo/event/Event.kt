package dev.uten2c.strobo.event

import dev.uten2c.strobo.Strobo

abstract class Event {

    fun callEvent(): Boolean {
        val set = Strobo.eventListeners.getOrDefault(this::class, HashSet())
        set.forEach { it.handler(this) }
        Strobo.eventListeners[this::class] = set
        return if (this is CancellableEvent) !isCancelled else true
    }
}
