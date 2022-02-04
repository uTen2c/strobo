package dev.uten2c.strobo.util

import net.minecraft.text.LiteralText

/**
 * [LiteralText]のコンストラクタのシンタックスシュガー
 */
fun text(string: String) = LiteralText(string)

/**
 * [LiteralText]のコンストラクタのシンタックスシュガー
 */
fun text(char: Char) = LiteralText(char.toString())

/**
 * [LiteralText]のコンストラクタのシンタックスシュガー
 */
fun text(number: Number) = LiteralText(number.toString())

/**
 * 空文字列の[LiteralText]を生成
 */
fun emptyText(): LiteralText = LiteralText("")
