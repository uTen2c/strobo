package dev.uten2c.strobo.serversideitem

sealed class RenderType {
    object Inventory : RenderType()
    class WithEntity(val entityId: Int) : RenderType()
}
