package dev.uten2c.strobo.serversideitem

/**
 * アイテムがパケットで送られる際の形式
 */
sealed class RenderType {
    /**
     * インベントリーのスロットの表示
     */
    object Inventory : RenderType()

    /**
     * それ以外のエンティティに関する表示。例えば、額縁
     */
    class WithEntity(val entityId: Int) : RenderType()
}
