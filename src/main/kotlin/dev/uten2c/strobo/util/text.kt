package dev.uten2c.strobo.util

import net.minecraft.nbt.NbtString
import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * [Text.literal]のシンタックスシュガー
 */
fun text(string: String): MutableText = Text.literal(string)

/**
 * [Text.literal]のシンタックスシュガー
 */
fun text(char: Char): MutableText = Text.literal(char.toString())

/**
 * [Text.literal]のシンタックスシュガー
 */
fun text(number: Number): MutableText = Text.literal(number.toString())

/**
 * 空文字列の[MutableText]を生成
 */
fun emptyText(): MutableText = Text.empty()

/**
 * [Text]をJsonに変換する
 */
fun Text.toJson(): String = Text.Serializer.toJson(this)

/**
 * [Text]を[NbtString]に変換する
 */
fun Text.toNbt(): NbtString = NbtString.of(toJson())
