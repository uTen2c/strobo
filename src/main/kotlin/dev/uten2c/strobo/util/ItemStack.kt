package dev.uten2c.strobo.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text

/**
 * [ItemStack]のカスタムモデルデータを扱う
 */
var ItemStack.customModelData: Int?
    get() = nbt?.getInt("CustomModelData")
    set(value) = when {
        value != null -> orCreateNbt.putInt("CustomModelData", value)
        else -> orCreateNbt.remove("CustomModelData")
    }

/**
 * [ItemStack]の説明欄を扱う
 */
var ItemStack.lore: List<Text>?
    get() {
        val nbt = getSubNbt("display") ?: return null
        val nbtList = nbt.getList("Lore", 8) ?: return null
        return nbtList.mapNotNull { Text.Serializer.fromJson(it.asString()) }.toList()
    }
    set(value) {
        if (value == null) {
            getSubNbt("display")?.remove("Lore")
        } else {
            val tag = getOrCreateSubNbt("display")
            val list = NbtList()
            list.addAll(value.map(Text::toNbt))
            tag.put("Lore", list)
        }
    }

/**
 * [ItemStack]が同じものかを判定
 */
fun ItemStack.isSimilar(stack: ItemStack?): Boolean {
    return when (stack) {
        null -> false
        this -> true
        else -> getItem() == stack.getItem() && damage == stack.damage && nbt == stack.nbt
    }
}
