package dev.uten2c.strobo.util

import net.minecraft.text.LiteralText

/**
 * [LiteralText]のコンストラクタのエイリアス
 */
fun text(string: String) = LiteralText(string)

/**
 * [LiteralText]のコンストラクタのエイリアス
 */
fun text(char: Char) = LiteralText(char.toString())

/**
 * 空文字列の[LiteralText]を生成
 */
fun emptyText(): LiteralText = LiteralText("")
