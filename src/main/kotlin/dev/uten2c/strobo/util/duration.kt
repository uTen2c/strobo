package dev.uten2c.strobo.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Tickを[Duration]に変換
 */
val Int.ticks: Duration
    get() = this.toLong().ticks

/**
 * Tickを[Duration]に変換
 */
val Long.ticks: Duration
    get() = (this * 50L).milliseconds
