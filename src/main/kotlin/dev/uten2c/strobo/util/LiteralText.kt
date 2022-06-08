package dev.uten2c.strobo.util

import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * [MutableText]のコンストラクタのシンタックスシュガー
 */
fun text(string: String): MutableText = Text.literal(string)

/**
 * [MutableText]のコンストラクタのシンタックスシュガー
 */
fun text(char: Char): MutableText = Text.literal(char.toString())

/**
 * [MutableText]のコンストラクタのシンタックスシュガー
 */
fun text(number: Number): MutableText = Text.literal(number.toString())

/**
 * 空文字列の[MutableText]を生成
 */
fun emptyText(): MutableText = Text.literal("")
