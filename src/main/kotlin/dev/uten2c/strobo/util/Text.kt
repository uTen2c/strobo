package dev.uten2c.strobo.util

import net.minecraft.nbt.NbtString
import net.minecraft.text.Text

fun Text.toJson(): String = Text.Serializer.toJson(this)

fun Text.toNbt(): NbtString = NbtString.of(toJson())