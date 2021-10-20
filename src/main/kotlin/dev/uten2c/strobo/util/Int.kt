package dev.uten2c.strobo.util

/**
 * 秒をティックに変換
 */
val Int.ticks: Long get() = this * 50L
