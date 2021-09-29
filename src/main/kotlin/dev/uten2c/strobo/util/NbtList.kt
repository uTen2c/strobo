package dev.uten2c.strobo.util

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

fun nbtListOf(vararg elements: NbtElement) = NbtList().apply {
    elements.forEach { add(it) }
}