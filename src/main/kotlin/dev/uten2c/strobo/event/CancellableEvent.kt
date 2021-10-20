package dev.uten2c.strobo.event

/**
 * [Event]の拡張。イベントをキャンセルできる。
 */
abstract class CancellableEvent(open var isCancelled: Boolean = false) : Event()
