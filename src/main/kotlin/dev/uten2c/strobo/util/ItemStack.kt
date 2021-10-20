package dev.uten2c.strobo.util

import net.minecraft.item.ItemStack

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
 * [ItemStack]が同じものかを判定
 */
fun ItemStack.isSimilar(stack: ItemStack?): Boolean {
    return when (stack) {
        null -> false
        this -> true
        else -> getItem() == stack.getItem() && damage == stack.damage && nbt == stack.nbt
    }
}
