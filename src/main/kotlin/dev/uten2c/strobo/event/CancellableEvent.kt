package dev.uten2c.strobo.event

abstract class CancellableEvent(var isCancelled: Boolean = false) : Event()
