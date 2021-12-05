package dev.uten2c.strobo.event

/**
 * イベントの優先度
 * LOWESTからMONITORの順に呼び出される。
 * MONITORは基本デバッグ用のつもりなので一番優先度高くしたい場合はHIGHESTを使うこと。
 */
enum class EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}
