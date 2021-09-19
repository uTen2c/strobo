package dev.uten2c.strobo.util

import net.minecraft.item.ItemStack

var ItemStack.customModelData: Int?
    get() = nbt?.getInt("CustomModelData")
    set(value) = when {
        value != null -> orCreateNbt.putInt("CustomModelData", value)
        else -> orCreateNbt.remove("CustomModelData")
    }

fun ItemStack.isSimilar(stack: ItemStack?): Boolean {
    return when (stack) {
        null -> false
        this -> true
        else -> getItem() == stack.getItem() && damage == stack.damage && nbt == stack.nbt
    }
}