package dev.uten2c.strobo.serversideitem

import dev.uten2c.strobo.mixin.accessor.ItemStackAccessor
import dev.uten2c.strobo.util.italic
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
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
    @Suppress("DEPRECATION", "CAST_NEVER_SUCCEEDS")
    fun createVisualStack(original: ItemStack, player: ServerPlayerEntity, renderType: RenderType): ItemStack {
        val stack = original.copy()
        val item = stack.item
        val id = Registry.ITEM.getId(item)
        (stack as ItemStackAccessor).setItem(visualItem)
        if (!stack.hasCustomName()) {
            stack.setCustomName((this as Item).name.copy().italic(false))
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
