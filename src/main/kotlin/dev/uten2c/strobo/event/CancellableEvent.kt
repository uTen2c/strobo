package dev.uten2c.strobo.event

abstract class CancellableEvent(open var isCancelled: Boolean = false) : Event()
