package dev.uten2c.strobo.serversideitem

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.registry.Registry

interface ServerSideItem {

    val visualItem: Item

    @Suppress("DEPRECATION")
    fun createVisualStack(itemStack: ItemStack): ItemStack {
        val stack: ItemStack = itemStack.copy()
        val item: Item = stack.getItem()
        val id = Registry.ITEM.getId(item)
        val translationKey = "item." + id.namespace + "." + id.path
        val customName: Text = TranslatableText(translationKey).setStyle(Style.EMPTY.withItalic(false))
        stack.item = visualItem
        if (!stack.hasCustomName()) {
            stack.setCustomName(customName)
        }
        val tag = stack.orCreateNbt
        tag.putString(TAG_KEY, id.toString())
        return stack
    }

    companion object {
        const val TAG_KEY = "0cf10e31-a339-43ca-9785-a01beb08e008"
    }
}