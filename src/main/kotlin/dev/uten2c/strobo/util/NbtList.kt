package dev.uten2c.strobo.util

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

/**
 * [NbtList]を生成する
 * @return 生成された[NbtList]
 */
fun nbtListOf(vararg elements: NbtElement) = NbtList().apply {
    elements.forEach { add(it) }
}
