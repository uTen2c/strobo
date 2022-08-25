package dev.uten2c.strobo.event

/**
 * イベントの優先度
 * [LOWEST]から[MONITOR]の順に呼び出される。
 * [MONITOR]は基本デバッグ用のつもりなので一番優先度高くしたい場合は[HIGHEST]を使うこと。
 */
enum class EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}
