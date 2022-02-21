package dev.uten2c.strobo.serversideitem

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

/**
 * [Item]と一緒に実装しなければならない
 */
interface ServerSideItem {

    /**
     * プレイヤーに送信されるアイテム
     */
    val visualItem: Item

    /**
     * プレイヤーに送信されるアイテムを生成する
     * @return 生成された[ItemStack]
     */
    @Suppress("DEPRECATION")
    fun createVisualStack(itemStack: ItemStack): ItemStack {
        val stack = itemStack.copy()
        val item = stack.getItem()
        val id = Registry.ITEM.getId(item)
        stack.item = visualItem
        if (!stack.hasCustomName()) {
            stack.setCustomName((this as Item).name)
        }
        val tag = stack.orCreateNbt
        tag.putString(TAG_KEY, id.toString())
        return stack
    }

    companion object {
        /**
         * サーバーサイドアイテムを識別するタグのID
         */
        const val TAG_KEY = "0cf10e31-a339-43ca-9785-a01beb08e008"
    }
}
