package dev.uten2c.strobo.util

import net.minecraft.nbt.NbtString
import net.minecraft.text.Text

/**
 * [Text]をJsonに変換する
 */
fun Text.toJson(): String = Text.Serializer.toJson(this)

/**
 * [Text]を[NbtString]に変換する
 */
fun Text.toNbt(): NbtString = NbtString.of(toJson())
