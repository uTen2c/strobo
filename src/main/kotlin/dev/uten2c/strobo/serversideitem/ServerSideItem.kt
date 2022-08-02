package dev.uten2c.strobo.serversideitem

import dev.uten2c.strobo.mixin.accessor.ItemStackAccessor
import dev.uten2c.strobo.util.italic
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

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
    @Suppress("CAST_NEVER_SUCCEEDS")
    fun createVisualStack(original: ItemStack, player: ServerPlayerEntity, renderType: RenderType): ItemStack {
        val stack = original.copy()
        (stack as ItemStackAccessor).setItem(visualItem)
        if (!stack.hasCustomName()) {
            stack.setCustomName((this as Item).name.copy().italic(false))
        }
        return stack
    }
}
